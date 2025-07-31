package com.capacitorjs.plugins.camera;

import static com.capacitorjs.plugins.camera.DeviceUtils.dpToPx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ZoomState;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("FieldCanBeLocal")
public class CameraFragment extends Fragment {

    // Constants
    @SuppressWarnings("unused")
    private final String TAG = "CameraFragment";
    private final String FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS";
    private final String PHOTO_TYPE = "image/jpeg";

    private final String CONFIRM_CANCEL_MESSAGE = "Are you sure?";
    private final String CONFIRM_CANCEL_POSITIVE = "Yes";
    private final String CONFIRM_CANCEL_NEGATIVE = "No";

    @ColorInt
    private final int ZOOM_TAB_LAYOUT_BACKGROUND_COLOR = 0x80000000;

    @ColorInt
    private final int ZOOM_BUTTON_COLOR_SELECTED = 0xFFFFFFFF;

    @ColorInt
    private final int ZOOM_BUTTON_COLOR_UNSELECTED = 0x80FFFFFF;

    private final AtomicBoolean isSnappingZoom = new AtomicBoolean(false);
    // View related variables
    private RelativeLayout relativeLayout;
    private RelativeLayout bottomBar;
    private PreviewView previewView;
    private ImageView focusIndicator;
    private ThumbnailAdapter thumbnailAdapter;
    private RecyclerView filmstripView;
    private TabLayout zoomTabLayout;
    private CardView zoomTabCardView;
    private FloatingActionButton takePictureButton;
    private FloatingActionButton flipCameraButton;
    private FloatingActionButton doneButton;
    private FloatingActionButton closeButton;
    private FloatingActionButton flashButton;
    private RelativeLayout.LayoutParams bottomBarLayoutParams;
    private RelativeLayout.LayoutParams cardViewLayoutParams;
    private RelativeLayout.LayoutParams tabLayoutParams;
    private RelativeLayout.LayoutParams takePictureLayoutParams;
    private RelativeLayout.LayoutParams flipButtonLayoutParams;
    private RelativeLayout.LayoutParams doneButtonLayoutParams;
    private RelativeLayout.LayoutParams closeButtonLayoutParams;
    private RelativeLayout.LayoutParams flashButtonLayoutParams;
    private DisplayMetrics displayMetrics;
    // Camera variables
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private int flashMode = ImageCapture.FLASH_MODE_AUTO;

    @SuppressWarnings("unused")
    private ZoomState zoomRatio = null;

    private float minZoom = 0f;

    @SuppressWarnings("unused")
    private float maxZoom = 1f;

    private ExecutorService cameraExecutor;
    private LifecycleCameraController cameraController;
    // Utility variables
    private HashMap<Uri, Bitmap> images;
    private ArrayList<ZoomTab> zoomTabs;

    private Handler zoomHandler = null;
    private Runnable zoomRunnable = null;
    private MediaActionSound mediaActionSound;

    // Callbacks
    private OnImagesCapturedCallback imagesCapturedCallback;

    @NonNull
    private static ColorStateList createButtonColorList() {
        int[][] states = new int[][] {
            new int[] { android.R.attr.state_enabled }, // enabled
            new int[] { -android.R.attr.state_enabled }, // disabled
            new int[] { -android.R.attr.state_checked }, // unchecked
            new int[] { android.R.attr.state_pressed } // pressed
        };

        int[] colors = new int[] { Color.DKGRAY, Color.TRANSPARENT, Color.TRANSPARENT, Color.LTGRAY };
        return new ColorStateList(states, colors);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        images = new HashMap<>();
        zoomTabs = new ArrayList<>();
        zoomHandler = new Handler(requireActivity().getMainLooper());
        mediaActionSound = new MediaActionSound();
        mediaActionSound.load(MediaActionSound.SHUTTER_CLICK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Restore the original system UI settings
        Window window = requireActivity().getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.show(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
            }
        } else {
            View decorView = window.getDecorView();
            int flags = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(flags);
        }
        window.setStatusBarColor(requireActivity().getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(requireActivity().getResources().getColor(android.R.color.transparent));

        if (mediaActionSound != null) {
            mediaActionSound.release();
            mediaActionSound = null;
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity fragmentActivity = requireActivity();
        displayMetrics = fragmentActivity.getResources().getDisplayMetrics();
        int margin = (int) (20 * displayMetrics.density);
        int barHeight = (int) (100 * displayMetrics.density);

        relativeLayout = new RelativeLayout(fragmentActivity);

        ColorStateList buttonColors = createButtonColorList();

        // Create the bottom bar and the buttons that sit inside it
        createBottomBar(fragmentActivity, barHeight, margin, buttonColors);

        // Camera preview is above the bottom bar. The zoom buttons and filmstrip overlap it
        createPreviewView(fragmentActivity);

        createFocusIndicator(fragmentActivity);

        // Zoom bar is above the bottom bar/buttons
        createZoomTabLayout(fragmentActivity, margin);

        // Thumbnail images in the filmstrip are above the zoom buttons
        createFilmstripView(fragmentActivity);

        // Close button and flash are top left/right corners
        createCloseButton(fragmentActivity, margin, buttonColors);
        createFlashButton(fragmentActivity, margin, buttonColors);

        // Set a transparent navigation bar
        Window window = requireActivity().getWindow();
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);

        // Enable immersive fullscreen mode (hide status and navigation bars)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.hide(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            View decorView = window.getDecorView();
            int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(flags);
        }

        // Remove edge-to-edge insets handling for true fullscreen
        requireActivity().getWindow().setDecorFitsSystemWindows(true);

        return relativeLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Request focus for the root view to ensure it can receive touch events immediately.
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        cameraController = new LifecycleCameraController(requireActivity());
        cameraController.bindToLifecycle(requireActivity());
        previewView.setController(cameraController);
        cameraExecutor = Executors.newSingleThreadExecutor();
        relativeLayout.post(this::setupCamera);
    }

    private void cancel() {
        // When the user cancels the camera session, it should clean up all the photos that were
        // taken.
        for (Map.Entry<Uri, Bitmap> image : images.entrySet()) {
            deleteFile(image.getKey());
        }
        if (imagesCapturedCallback != null) {
            imagesCapturedCallback.onCaptureCanceled();
        }
        closeFragment();
    }

    private void done() {
        if (imagesCapturedCallback != null) {
            imagesCapturedCallback.onCaptureSuccess(images);
        }
        closeFragment();
    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public void setImagesCapturedCallback(OnImagesCapturedCallback imagesCapturedCallback) {
        this.imagesCapturedCallback = imagesCapturedCallback;
    }

    private void createBottomBar(FragmentActivity fragmentActivity, int barHeight, int margin, ColorStateList buttonColors) {
        bottomBar = new RelativeLayout(fragmentActivity);
        bottomBar.setId(View.generateViewId());
        bottomBar.setBackgroundColor(Color.BLACK);
        bottomBarLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, barHeight);
        bottomBarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomBar.setLayoutParams(bottomBarLayoutParams);
        relativeLayout.addView(bottomBar);

        createTakePictureButton(fragmentActivity, margin, buttonColors);
        createFlipButton(fragmentActivity, margin, buttonColors);
        createDoneButton(fragmentActivity, margin, buttonColors);
    }

    private void createFlashButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        flashButton = new FloatingActionButton(fragmentActivity);
        flashButton.setId(View.generateViewId());
        flashButton.setImageResource(R.drawable.flash_auto_24px);
        flashButton.setBackgroundTintList(buttonColors);
        flashButton.setColorFilter(Color.WHITE);
        flashButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        flashButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        flashButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int topMargin = (int) (margin * 2.5);
        flashButtonLayoutParams.setMargins(0, topMargin, margin, 0);
        flashButton.setLayoutParams(flashButtonLayoutParams);
        flashButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                flashMode = cameraController.getImageCaptureFlashMode();
                switch (flashMode) {
                    case ImageCapture.FLASH_MODE_OFF:
                        {
                            flashMode = ImageCapture.FLASH_MODE_ON;
                            flashButton.setImageResource(R.drawable.flash_on_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    case ImageCapture.FLASH_MODE_ON:
                        {
                            flashMode = ImageCapture.FLASH_MODE_AUTO;
                            flashButton.setImageResource(R.drawable.flash_auto_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    case ImageCapture.FLASH_MODE_AUTO:
                        {
                            flashMode = ImageCapture.FLASH_MODE_OFF;
                            flashButton.setImageResource(R.drawable.flash_off_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    default:
                        throw new IllegalStateException("Unexpected flash mode: " + flashMode);
                }
                cameraController.setImageCaptureFlashMode(flashMode);
            }
        );
        relativeLayout.addView(flashButton);
    }

    private void createTakePictureButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        takePictureButton = new FloatingActionButton(fragmentActivity);
        takePictureButton.setId(View.generateViewId());
        takePictureButton.setImageResource(R.drawable.ic_shutter_circle);
        takePictureButton.setBackgroundColor(Color.TRANSPARENT);
        takePictureButton.setBackgroundTintList(buttonColors);

        int fabSize = dpToPx(fragmentActivity, 84);
        int iconSize = (int) (fabSize * 0.9);
        takePictureButton.setCustomSize(fabSize);
        takePictureButton.setMaxImageSize(iconSize);

        takePictureLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        takePictureLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        takePictureButton.setLayoutParams(takePictureLayoutParams);
        takePictureButton.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(fragmentActivity, R.animator.button_press_animation));
        takePictureButton.setOnClickListener(
            v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                mediaActionSound.play(MediaActionSound.SHUTTER_CLICK);
                var name = new SimpleDateFormat(FILENAME, Locale.US).format(System.currentTimeMillis());
                var contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, PHOTO_TYPE);
                var outputOptions = new ImageCapture.OutputFileOptions.Builder(
                    requireContext().getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                    .build();

                cameraController.takePicture(
                    outputOptions,
                    cameraExecutor,
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Uri savedImageUri = outputFileResults.getSavedUri();
                            if (savedImageUri != null) {
                                try {
                                    InputStream stream = requireContext().getContentResolver().openInputStream(savedImageUri);
                                    Bitmap bmp = BitmapFactory.decodeStream(stream);
                                    images.put(savedImageUri, bmp);
                                    requireView()
                                        .post(
                                            () -> thumbnailAdapter.addThumbnail(savedImageUri, getThumbnail(savedImageUri))
                                        );
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {}
                    }
                );
            }
        );
        bottomBar.addView(takePictureButton);
    }

    private void createFlipButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        flipCameraButton = new FloatingActionButton(fragmentActivity);
        flipCameraButton.setId(View.generateViewId());
        flipCameraButton.setImageResource(R.drawable.flip_camera_android_24px);
        flipCameraButton.setColorFilter(Color.WHITE);
        flipCameraButton.setBackgroundTintList(buttonColors);
        flipButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        flipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        flipButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        flipButtonLayoutParams.setMargins(margin, 0, 0, 0);
        flipCameraButton.setLayoutParams(flipButtonLayoutParams);
        flipCameraButton.setOnClickListener(
            v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                lensFacing = lensFacing == CameraSelector.LENS_FACING_FRONT ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT;
                flashButton.setVisibility(lensFacing == CameraSelector.LENS_FACING_BACK ? View.VISIBLE : View.GONE);
                if (!zoomTabs.isEmpty()) {
                    zoomTabLayout.removeAllTabs();
                    zoomTabs.clear();
                }
                setupCamera();
            }
        );
        bottomBar.addView(flipCameraButton);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createPreviewView(FragmentActivity fragmentActivity) {
        previewView = new PreviewView(fragmentActivity);
        previewView.setId(View.generateViewId());

        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        previewLayoutParams.addRule(RelativeLayout.ABOVE, bottomBar.getId());
        previewView.setLayoutParams(previewLayoutParams);
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);

        previewView.setOnTouchListener(
            (v, event) -> {
                // Position the focus indicator at the touch point
                focusIndicator.setX(event.getX() - (focusIndicator.getWidth() / 2f));
                focusIndicator.setY(event.getY() - (focusIndicator.getHeight() / 2f));

                // Let the PreviewView handle the rest of the touch event.
                // Returning false allows the default tap-to-focus behavior to trigger.
                return false;
            }
        );

        relativeLayout.addView(previewView);
    }

    private void createFocusIndicator(Context context) {
        focusIndicator = new ImageView(context);
        focusIndicator.setImageResource(R.drawable.center_focus_24px);

        int size = dpToPx(context, 72);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
        focusIndicator.setLayoutParams(layoutParams);

        focusIndicator.setColorFilter(Color.WHITE);
        focusIndicator.setVisibility(View.INVISIBLE); // Initially hidden

        relativeLayout.addView(focusIndicator);
    }

    private void createZoomTabLayout(FragmentActivity fragmentActivity, int margin) {
        zoomTabCardView = new CardView(fragmentActivity);
        zoomTabCardView.setId(View.generateViewId());

        // Make the card view rounded corners
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setShape(GradientDrawable.RECTANGLE);
        backgroundDrawable.setColor(ZOOM_TAB_LAYOUT_BACKGROUND_COLOR);
        backgroundDrawable.setCornerRadius(dpToPx(requireContext(), 56 / 2));
        zoomTabCardView.setBackground(backgroundDrawable);

        // Define the LayoutParams for the cardView
        cardViewLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        cardViewLayoutParams.addRule(RelativeLayout.ABOVE, bottomBar.getId());
        cardViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cardViewLayoutParams.setMargins(margin, margin, margin, margin); // Adjust bottom margin as needed
        zoomTabCardView.setLayoutParams(cardViewLayoutParams);

        relativeLayout.addView(zoomTabCardView);

        zoomTabLayout = new TabLayout(fragmentActivity);
        zoomTabLayout.setId(View.generateViewId());
        tabLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomTabLayout.setLayoutParams(tabLayoutParams);

        // Set TabLayout parameters
        zoomTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        zoomTabLayout.setTabMode(TabLayout.MODE_FIXED);
        zoomTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT); // No indicator color
        zoomTabLayout.setSelectedTabIndicator(null);
        zoomTabLayout.setBackgroundColor(Color.TRANSPARENT); // Transparent background to let card view color show
        zoomTabLayout.setBackground(null);

        // Set the listener for tab selection to change the text color and background
        zoomTabLayout.addOnTabSelectedListener(
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(true);
                    if (!isSnappingZoom.get()) {
                        zoomTab.setTransientZoomLevel(null);
                        if (cameraController != null) {
                            cameraController.setZoomRatio(zoomTab.getZoomLevel());
                        }
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(false);
                    zoomTab.setTransientZoomLevel(null);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(true);
                    if (!isSnappingZoom.get()) {
                        zoomTab.setTransientZoomLevel(null);
                        if (cameraController != null) {
                            cameraController.setZoomRatio(zoomTab.getZoomLevel());
                        }
                    }
                }
            }
        );

        zoomTabCardView.addView(zoomTabLayout);
    }

    private void createZoomTabs(FragmentActivity fragmentActivity, TabLayout tabLayout) {
        float[] zoomLevels = { minZoom, 1f, 2f, 5f };

        for (int i = 0; i < zoomLevels.length; i++) {
            float zoomLevel = zoomLevels[i];
            ZoomTab zoomTab = new ZoomTab(fragmentActivity, zoomLevel, 40, i);
            zoomTabs.add(zoomTab);
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setCustomView(zoomTab.getView());
            tabLayout.addTab(tab);
        }

        tabLayout.selectTab(tabLayout.getTabAt(1));
    }

    private void createFilmstripView(FragmentActivity fragmentActivity) {
        filmstripView = new RecyclerView(fragmentActivity);
        RelativeLayout.LayoutParams filmstripLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        filmstripLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        filmstripLayoutParams.addRule(RelativeLayout.ABOVE, zoomTabCardView.getId());
        filmstripView.setLayoutParams(filmstripLayoutParams);

        // Add padding to the filmstrip to prevent clipping of the remove button
        int padding = dpToPx(fragmentActivity, 12);
        filmstripView.setPadding(padding, padding, padding, padding);
        filmstripView.setClipToPadding(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(fragmentActivity, LinearLayoutManager.HORIZONTAL, false);
        filmstripView.setLayoutManager(layoutManager);
        thumbnailAdapter = new ThumbnailAdapter();
        filmstripView.setAdapter(thumbnailAdapter);
        relativeLayout.addView(filmstripView);

        thumbnailAdapter.setOnThumbnailsChangedCallback(
            new ThumbnailAdapter.OnThumbnailsChangedCallback() {
                @Override
                public void onThumbnailRemoved(Uri uri, Bitmap bmp) {
                    images.remove(uri);
                    deleteFile(uri);
                }
            }
        );
    }

    private void createDoneButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        doneButton = new FloatingActionButton(fragmentActivity);
        doneButton.setId(View.generateViewId());
        doneButton.setImageResource(R.drawable.done_24px);
        doneButton.setColorFilter(Color.WHITE);
        doneButton.setBackgroundTintList(buttonColors);
        doneButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        doneButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        doneButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        doneButtonLayoutParams.setMargins(0, 0, margin, 0);
        doneButton.setLayoutParams(doneButtonLayoutParams);
        doneButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                done();
            }
        );
        bottomBar.addView(doneButton);
    }

    private void createCloseButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        closeButton = new FloatingActionButton(fragmentActivity);
        closeButton.setId(View.generateViewId());
        closeButton.setImageResource(R.drawable.close_24px);
        closeButton.setBackgroundTintList(buttonColors);
        closeButton.setColorFilter(Color.WHITE);
        closeButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // Increase top margin for immersive mode (e.g., 2.5x the original margin)
        int topMargin = (int) (margin * 2.5);
        closeButtonLayoutParams.setMargins(margin, topMargin, 0, 0);
        closeButton.setLayoutParams(closeButtonLayoutParams);
        closeButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (images != null && !images.isEmpty()) {
                    new AlertDialog.Builder(requireContext())
                    .setMessage(CONFIRM_CANCEL_MESSAGE)
                    .setPositiveButton(CONFIRM_CANCEL_POSITIVE, (dialogInterface, i) -> cancel())
                    .setNegativeButton(CONFIRM_CANCEL_NEGATIVE, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
                } else {
                    cancel();
                }
            }
        );
        relativeLayout.addView(closeButton);
    }

    private void deleteFile(Uri fileUri) {
        try {
            ContentResolver contentResolver = requireContext().getContentResolver();
            int deleted = contentResolver.delete(fileUri, null, null);

            if (deleted == 0) {
                // File deletion failed
                Log.e("Delete File", "Failed to delete file: " + fileUri);
            } else {
                // File deletion successful
                Log.i("Delete File", "File deleted: " + fileUri);
            }
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

    private void setupCamera() throws IllegalStateException {
        cameraController
            .getInitializationFuture()
            .addListener(() -> {
                if (!hasFrontFacingCamera()) {
                    flipCameraButton.setVisibility(View.GONE);
                }
            }, ContextCompat.getMainExecutor(requireContext()));

        cameraController
            .getZoomState()
            .observe(
                requireActivity(),
                zoomState -> {
                    zoomRatio = zoomState;
                    minZoom = zoomState.getMinZoomRatio();
                    maxZoom = zoomState.getMaxZoomRatio();

                    if (zoomTabs.isEmpty()) {
                        createZoomTabs(requireActivity(), zoomTabLayout);
                    }

                    if (zoomRunnable != null) {
                        zoomHandler.removeCallbacks(zoomRunnable);
                    }

                    zoomRunnable =
                        () -> {
                            float currentZoom = zoomRatio.getZoomRatio();
                            ZoomTab closestTab = null;
                            final float threshold = 0.05f; // Threshold for considering the next zoom level

                            for (int i = 0; i < zoomTabs.size(); i++) {
                                ZoomTab currentTab = zoomTabs.get(i);
                                // Check if this is the last tab or if the current zoom is less than the next tab's level minus the threshold
                                if (i == zoomTabs.size() - 1 || currentZoom < zoomTabs.get(i + 1).zoomLevel - threshold) {
                                    closestTab = currentTab;
                                    break;
                                }
                            }

                            // If we found a closest tab, update its display and select the tab.
                            if (closestTab != null) {
                                TabLayout.Tab tab = zoomTabLayout.getTabAt(closestTab.getTabIndex());
                                if (tab != null) {
                                    closestTab.setTransientZoomLevel(currentZoom); // Update the tab's display to show the current zoom level
                                    isSnappingZoom.set(true);
                                    zoomTabLayout.selectTab(tab); // This will not trigger the camera zoom change due to the isSnappingZoom flag
                                    isSnappingZoom.set(false);
                                }
                            }
                        };
                    zoomHandler.post(zoomRunnable);
                }
            );

        cameraController
            .getTapToFocusState()
            .observe(
                requireActivity(),
                tapToFocusState -> {
                    if (focusIndicator == null) return;
                    // Show and animate the focus indicator when focusing starts
                    if (tapToFocusState == LifecycleCameraController.TAP_TO_FOCUS_STARTED) {
                        focusIndicator.setVisibility(View.VISIBLE);
                        focusIndicator.setAlpha(0f); // Start fully transparent
                        focusIndicator.animate().alpha(1f).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    } else {
                        // Fade out and hide the focus indicator when focusing ends, regardless of the result
                        focusIndicator
                            .animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> focusIndicator.setVisibility(View.INVISIBLE))
                            .start();
                    }
                }
            );

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
        cameraController.setCameraSelector(cameraSelector);
        cameraController.setPinchToZoomEnabled(true);
        cameraController.setTapToFocusEnabled(true);
        cameraController.setImageCaptureFlashMode(flashMode);
    }

    private boolean hasFrontFacingCamera() {
        if (cameraController != null) {
            CameraSelector frontFacing = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();

            return cameraController.hasCamera(frontFacing);
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private Bitmap getThumbnail(Uri imageUri) {
        ContentResolver contentResolver = requireContext().getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // API level 29 and above
            try { // Specify the size of the thumbnail
                int width = (int) (displayMetrics.widthPixels * 0.25); // Thumbnail width as 25% of screen width
                int height = (int) (displayMetrics.heightPixels * 0.25); // Thumbnail height as 25% of screen height
                Size size = new Size(width, height);
                // Load the thumbnail
                return contentResolver.loadThumbnail(imageUri, size, null);
            } catch (IOException e) {
                // Handle exceptions
                e.printStackTrace();
                return null;
            }
        } else { // Below API level 29
            String[] projection = { MediaStore.Images.Media._ID };
            Cursor cursor = contentResolver.query(imageUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long imageId = cursor.getLong(idColumn);
                cursor.close();

                return MediaStore.Images.Thumbnails.getThumbnail(contentResolver, imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }
            return null;
        }
    }

    public abstract static class OnImagesCapturedCallback {

        public void onCaptureSuccess(HashMap<Uri, Bitmap> images) {}

        public void onCaptureCanceled() {}
    }

    public class ZoomTab {

        private final float zoomLevel;
        private final int tabIndex;
        private final int circleSize;
        private final TextView textView;
        private final GradientDrawable background;
        private Float transientZoomLevel;

        public ZoomTab(Context context, float zoomLevel, int circleSize, int tabIndex) {
            this.zoomLevel = zoomLevel;
            this.transientZoomLevel = null;
            this.textView = new TextView(context);
            this.background = new GradientDrawable();
            this.circleSize = circleSize;

            setupTextView();
            this.tabIndex = tabIndex;
        }

        private void setupTextView() {
            String formattedZoom = getFormattedZoom();
            textView.setGravity(Gravity.CENTER);
            textView.setText(formattedZoom);
            textView.setTextSize(12);
            textView.setBackgroundColor(Color.TRANSPARENT);

            int padding = dpToPx(requireContext(), 8);
            textView.setPadding(padding, padding, padding, padding);

            int circlePx = dpToPx(requireContext(), circleSize);
            background.setShape(GradientDrawable.OVAL);
            background.setSize(circlePx, circlePx); // Make it circular

            textView.setBackground(background);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textView.setLayoutParams(layoutParams);

            setSelected(false);
        }

        @NonNull
        private String getFormattedZoom() {
            String formattedZoom;

            float whichZoom = transientZoomLevel == null ? zoomLevel : transientZoomLevel;

            if (whichZoom < 0) {
                formattedZoom = "0x"; // Handle zoom levels less than 0
            } else if (whichZoom < 10) {
                DecimalFormat formatLessThanTen = new DecimalFormat("#.#x");
                formattedZoom = formatLessThanTen.format(whichZoom); // At most 1 digit after the decimal point if less than 10
            } else {
                DecimalFormat formatTenOrMore = new DecimalFormat("#x");
                formattedZoom = formatTenOrMore.format(whichZoom); // No decimal point if 10 or greater
            }
            return formattedZoom;
        }

        public View getView() {
            return textView;
        }

        public void setSelected(boolean isSelected) {
            textView.setTextColor(isSelected ? Color.BLACK : Color.WHITE);
            background.setColor(isSelected ? ZOOM_BUTTON_COLOR_SELECTED : ZOOM_BUTTON_COLOR_UNSELECTED);
        }

        public float getZoomLevel() {
            return zoomLevel;
        }

        public int getTabIndex() {
            return tabIndex;
        }

        public void setTransientZoomLevel(Float zoomLevel) {
            transientZoomLevel = zoomLevel;
            updateText();
        }

        private void updateText() {
            String formattedZoom = getFormattedZoom();
            textView.setText(formattedZoom);
        }
    }
}

