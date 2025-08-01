import UIKit
import AVFoundation
import Photos
import Capacitor

// MARK: - ThumbnailCell
class ThumbnailCell: UICollectionViewCell {
    var deleteHandler: (() -> Void)?

    private let imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFill
        imageView.clipsToBounds = true
        imageView.layer.cornerRadius = 5
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()

    private let loadingIndicator: UIActivityIndicatorView = {
        let indicator = UIActivityIndicatorView(style: .medium)
        indicator.color = .white
        indicator.translatesAutoresizingMaskIntoConstraints = false
        indicator.hidesWhenStopped = true
        return indicator
    }()

    private let deleteButton: UIButton = {
        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: "xmark.circle.fill"), for: .normal)
        button.tintColor = .white
        button.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        button.layer.cornerRadius = 10
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupUI()
    }

    private func setupUI() {
        contentView.addSubview(imageView)
        contentView.addSubview(loadingIndicator)
        contentView.addSubview(deleteButton)

        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: contentView.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),

            loadingIndicator.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            loadingIndicator.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),

            deleteButton.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 2),
            deleteButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -2),
            deleteButton.widthAnchor.constraint(equalToConstant: 20),
            deleteButton.heightAnchor.constraint(equalToConstant: 20)
        ])

        deleteButton.addTarget(self, action: #selector(deleteButtonTapped), for: .touchUpInside)
    }

    func configure(with image: UIImage) {
        imageView.image = image
        loadingIndicator.stopAnimating()
        deleteButton.isHidden = false
    }

    func configureAsLoading() {
        imageView.image = nil
        imageView.backgroundColor = UIColor.darkGray.withAlphaComponent(0.8)
        loadingIndicator.startAnimating()
        deleteButton.isHidden = true
    }

    @objc private func deleteButtonTapped() {
        deleteHandler?()
    }
}

// MARK: - ImagePreviewViewController
class ImagePreviewViewController: UIViewController {
    private let images: [UIImage]
    private var currentIndex: Int

    private lazy var collectionView: UICollectionView = {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        layout.minimumLineSpacing = 0
        layout.minimumInteritemSpacing = 0

        let collectionView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        collectionView.backgroundColor = .black
        collectionView.isPagingEnabled = true
        collectionView.showsHorizontalScrollIndicator = false
        collectionView.register(ImagePreviewCell.self, forCellWithReuseIdentifier: "ImagePreviewCell")
        collectionView.dataSource = self
        collectionView.delegate = self
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        return collectionView
    }()

    private lazy var positionIndicator: UILabel = {
        let label = UILabel()
        label.textColor = .white
        label.textAlignment = .center
        label.font = UIFont.systemFont(ofSize: 14, weight: .medium)
        label.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        label.layer.cornerRadius = 12
        label.layer.masksToBounds = true
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    init(images: [UIImage], startingIndex: Int = 0) {
        self.images = images
        self.currentIndex = max(0, min(startingIndex, images.count - 1))
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .black
        setupUI()
        updatePositionIndicator()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        // Scroll to the starting index after layout is complete
        if let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout {
            layout.itemSize = collectionView.bounds.size
            collectionView.scrollToItem(at: IndexPath(item: currentIndex, section: 0), at: .left, animated: false)
        }
    }

    private func setupUI() {
        view.addSubview(collectionView)
        view.addSubview(positionIndicator)

        NSLayoutConstraint.activate([
            collectionView.topAnchor.constraint(equalTo: view.topAnchor),
            collectionView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            collectionView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            collectionView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])

        // Setup close button
        let closeButton = UIButton(type: .system)
        closeButton.setImage(UIImage(systemName: "xmark"), for: .normal)
        closeButton.tintColor = .white
        closeButton.translatesAutoresizingMaskIntoConstraints = false
        closeButton.addTarget(self, action: #selector(closeButtonTapped), for: .touchUpInside)

        view.addSubview(closeButton)
        NSLayoutConstraint.activate([
            closeButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
            closeButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            closeButton.widthAnchor.constraint(equalToConstant: 44),
            closeButton.heightAnchor.constraint(equalToConstant: 44)
        ])

        // Position indicator constraints - only show if more than 1 image
        if images.count > 1 {
            NSLayoutConstraint.activate([
                positionIndicator.centerXAnchor.constraint(equalTo: view.centerXAnchor),
                positionIndicator.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
                positionIndicator.widthAnchor.constraint(greaterThanOrEqualToConstant: 40),
                positionIndicator.heightAnchor.constraint(equalToConstant: 24)
            ])
        } else {
            positionIndicator.isHidden = true
        }
    }

    private func updatePositionIndicator() {
        if images.count > 1 {
            positionIndicator.text = "\(currentIndex + 1)/\(images.count)"
        }
    }

    @objc private func closeButtonTapped() {
        dismiss(animated: true)
    }
}

// MARK: - ImagePreviewViewController Extensions
extension ImagePreviewViewController: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return images.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ImagePreviewCell", for: indexPath) as? ImagePreviewCell else {
            return UICollectionViewCell()
        }

        cell.configure(with: images[indexPath.item])
        return cell
    }
}

extension ImagePreviewViewController: UICollectionViewDelegate {
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let pageWidth = scrollView.frame.width
        let currentPage = Int(scrollView.contentOffset.x / pageWidth)

        if currentPage != currentIndex {
            currentIndex = currentPage
            updatePositionIndicator()
        }
    }
}

// MARK: - ImagePreviewCell
class ImagePreviewCell: UICollectionViewCell {
    private let imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupUI()
    }

    private func setupUI() {
        contentView.addSubview(imageView)
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: contentView.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor)
        ])
    }

    func configure(with image: UIImage) {
        imageView.image = image
    }
}

protocol MultiCameraViewControllerDelegate: AnyObject {
    func multiCameraViewController(_ viewController: MultiCameraViewController, didFinishWith images: [UIImage], metadata: [[String: Any]])
    func multiCameraViewControllerDidCancel(_ viewController: MultiCameraViewController)
}

class MultiCameraViewController: UIViewController {
    // MARK: - Properties
    weak var delegate: MultiCameraViewControllerDelegate?
    var maxImages: Int = 0 // 0 means unlimited
    var cameraDirection: CameraDirection = .rear

    private var captureSession: AVCaptureSession?
    private var previewLayer: AVCaptureVideoPreviewLayer?
    private var photoOutput: AVCapturePhotoOutput?
    private var currentCameraPosition: AVCaptureDevice.Position = .back
    private var flashMode: AVCaptureDevice.FlashMode = .auto

    // Zoom control properties
    private var currentZoomFactor: CGFloat = 1.0
    private var minZoomFactor: CGFloat = 1.0
    private var maxZoomFactor: CGFloat = 10.0
    private var lastZoomFactor: CGFloat = 1.0

    private var capturedImages: [UIImage] = []
    private var capturedMetadata: [[String: Any]] = []
    private var loadingStates: [Bool] = [] // Track which thumbnails are still loading

    // Track device orientation
    private var isLandscape: Bool = false
    private var portraitConstraints: [NSLayoutConstraint] = []
    private var landscapeConstraints: [NSLayoutConstraint] = []

    // MARK: - UI Elements
    private lazy var previewView: UIView = {
        let view = UIView()
        view.backgroundColor = .black
        view.contentMode = .scaleAspectFill
        view.clipsToBounds = true
        return view
    }()

    private lazy var bottomBarView: UIView = {
        let view = UIView()
        view.backgroundColor = .black
        return view
    }()

    private lazy var takePictureButton: UIButton = {
        let button = UIButton(type: .custom)
        // Try to load the image from the bundle
        if let image = UIImage(named: "camera_capture") {
            button.setImage(image, for: .normal)
        } else {
            // Fallback if image is not found
            button.backgroundColor = .white
            button.layer.cornerRadius = 35
            button.layer.borderWidth = 3
            button.layer.borderColor = UIColor.lightGray.cgColor
        }
        button.addTarget(self, action: #selector(takePicture), for: .touchUpInside)
        return button
    }()

    private lazy var flipCameraButton: UIButton = {
        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: "camera.rotate"), for: .normal)
        button.tintColor = .white
        button.addTarget(self, action: #selector(flipCamera), for: .touchUpInside)
        return button
    }()

    private lazy var flashButton: UIButton = {
        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: "bolt.badge.a"), for: .normal)
        button.tintColor = .white
        button.addTarget(self, action: #selector(toggleFlash), for: .touchUpInside)
        return button
    }()

    private lazy var zoomInButton: UIButton = {
        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: "plus.magnifyingglass"), for: .normal)
        button.tintColor = .white
        button.addTarget(self, action: #selector(zoomIn), for: .touchUpInside)
        return button
    }()

    private lazy var zoomOutButton: UIButton = {
        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: "minus.magnifyingglass"), for: .normal)
        button.tintColor = .white
        button.addTarget(self, action: #selector(zoomOut), for: .touchUpInside)
        return button
    }()

    private lazy var zoomFactorLabel: UILabel = {
        let label = UILabel()
        label.textColor = .white
        label.textAlignment = .center
        label.font = UIFont.systemFont(ofSize: 12)
        label.text = "1.0x"
        label.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        label.layer.cornerRadius = 8
        label.layer.masksToBounds = true
        return label
    }()

    private lazy var closeButton: UIButton = {
        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: "xmark"), for: .normal)
        button.tintColor = .white
        button.addTarget(self, action: #selector(cancel), for: .touchUpInside)
        return button
    }()

    private lazy var doneButton: UIButton = {
        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: "checkmark"), for: .normal)
        button.tintColor = .white
        button.addTarget(self, action: #selector(done), for: .touchUpInside)
        return button
    }()

    private lazy var processingSpinner: UIActivityIndicatorView = {
        let spinner = UIActivityIndicatorView(style: .large)
        spinner.color = .white
        spinner.backgroundColor = UIColor.black.withAlphaComponent(0.7)
        spinner.layer.cornerRadius = 10
        spinner.translatesAutoresizingMaskIntoConstraints = false
        spinner.hidesWhenStopped = true
        return spinner
    }()

    private lazy var processingLabel: UILabel = {
        let label = UILabel()
        label.text = "Processing images..."
        label.textColor = .white
        label.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    private lazy var processingOverlay: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.black.withAlphaComponent(0.7)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.isHidden = true
        return view
    }()

    private lazy var thumbnailCollectionView: UICollectionView = {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        layout.itemSize = CGSize(width: 70, height: 70)
        layout.minimumLineSpacing = 5

        let collectionView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        collectionView.backgroundColor = .clear
        collectionView.showsHorizontalScrollIndicator = false
        collectionView.register(ThumbnailCell.self, forCellWithReuseIdentifier: "ThumbnailCell")
        collectionView.dataSource = self
        collectionView.delegate = self
        return collectionView
    }()

    // MARK: - Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        checkPermissions()

        // Set background color to ensure we can see if there's an issue with the camera
        view.backgroundColor = .black

        // Add pinch gesture for zoom
        let pinchGesture = UIPinchGestureRecognizer(target: self, action: #selector(handlePinchGesture(_:)))
        previewView.addGestureRecognizer(pinchGesture)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        startCaptureSession()
        updatePreviewLayerFrame()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updatePreviewLayerFrame()
    }

    private func updatePreviewLayerFrame() {
        guard let previewLayer = previewLayer else { return }
        previewLayer.frame = previewView.bounds

        // Update video orientation based on device orientation
        guard let connection = previewLayer.connection else { return }

        if connection.isVideoOrientationSupported {
            let orientation = UIDevice.current.orientation

            switch orientation {
            case .portrait:
                connection.videoOrientation = .portrait
            case .landscapeLeft:
                connection.videoOrientation = .landscapeRight // Note: device orientation is opposite to video orientation
            case .landscapeRight:
                connection.videoOrientation = .landscapeLeft // Note: device orientation is opposite to video orientation
            case .portraitUpsideDown:
                connection.videoOrientation = .portraitUpsideDown
            default:
                connection.videoOrientation = isLandscape ? .landscapeRight : .portrait
            }
        }
    }

    private func updateConstraintsForOrientation() {
        // Deactivate all constraints first
        NSLayoutConstraint.deactivate(portraitConstraints)
        NSLayoutConstraint.deactivate(landscapeConstraints)

        // Activate the appropriate constraints based on orientation
        if isLandscape {
            NSLayoutConstraint.activate(landscapeConstraints)
        } else {
            NSLayoutConstraint.activate(portraitConstraints)
        }

        // Force layout update
        view.layoutIfNeeded()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        stopCaptureSession()
    }

    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)

        // Determine if we're switching to landscape or portrait
        isLandscape = size.width > size.height

        // Handle orientation changes
        coordinator.animate(alongsideTransition: { [weak self] _ in
            guard let self = self else { return }
            self.updatePreviewLayerFrame()
            self.updateConstraintsForOrientation()
        })
    }

    // MARK: - Setup
    private func setupUI() {
        view.backgroundColor = .black

        // Add subviews
        view.addSubview(previewView)
        view.addSubview(bottomBarView)
        view.addSubview(thumbnailCollectionView)
        view.addSubview(closeButton)
        view.addSubview(flashButton)
        view.addSubview(zoomInButton)
        view.addSubview(zoomOutButton)
        view.addSubview(zoomFactorLabel)
        view.addSubview(processingOverlay)
        
        // Add processing overlay subviews
        processingOverlay.addSubview(processingSpinner)
        processingOverlay.addSubview(processingLabel)

        bottomBarView.addSubview(takePictureButton)
        bottomBarView.addSubview(flipCameraButton)
        bottomBarView.addSubview(doneButton)

        // Setup constraints
        previewView.translatesAutoresizingMaskIntoConstraints = false
        bottomBarView.translatesAutoresizingMaskIntoConstraints = false
        thumbnailCollectionView.translatesAutoresizingMaskIntoConstraints = false
        takePictureButton.translatesAutoresizingMaskIntoConstraints = false
        flipCameraButton.translatesAutoresizingMaskIntoConstraints = false
        doneButton.translatesAutoresizingMaskIntoConstraints = false
        closeButton.translatesAutoresizingMaskIntoConstraints = false
        flashButton.translatesAutoresizingMaskIntoConstraints = false
        zoomInButton.translatesAutoresizingMaskIntoConstraints = false
        zoomOutButton.translatesAutoresizingMaskIntoConstraints = false
        zoomFactorLabel.translatesAutoresizingMaskIntoConstraints = false
        
        // Setup processing overlay constraints
        NSLayoutConstraint.activate([
            processingOverlay.topAnchor.constraint(equalTo: view.topAnchor),
            processingOverlay.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            processingOverlay.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            processingOverlay.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            
            processingSpinner.centerXAnchor.constraint(equalTo: processingOverlay.centerXAnchor),
            processingSpinner.centerYAnchor.constraint(equalTo: processingOverlay.centerYAnchor, constant: -20),
            
            processingLabel.topAnchor.constraint(equalTo: processingSpinner.bottomAnchor, constant: 20),
            processingLabel.centerXAnchor.constraint(equalTo: processingOverlay.centerXAnchor)
        ])

        // Determine initial orientation
        isLandscape = UIDevice.current.orientation.isLandscape

        // Create portrait constraints
        setupPortraitConstraints()

        // Create landscape constraints
        setupLandscapeConstraints()

        // Activate the appropriate constraints based on current orientation
        updateConstraintsForOrientation()

        // Initially hide the done button until we have at least one image
        doneButton.isHidden = true
    }

    private func setupPortraitConstraints() {
        // Clear any existing constraints
        portraitConstraints.removeAll()

        // Add common constraints that don't change with orientation
        let commonConstraints = [
            // Preview view top, leading, trailing
            previewView.topAnchor.constraint(equalTo: view.topAnchor),
            previewView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            previewView.trailingAnchor.constraint(equalTo: view.trailingAnchor),

            // Close button
            closeButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 10),
            closeButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            closeButton.widthAnchor.constraint(equalToConstant: 44),
            closeButton.heightAnchor.constraint(equalToConstant: 44),

            // Flash button
            flashButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 10),
            flashButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            flashButton.widthAnchor.constraint(equalToConstant: 44),
            flashButton.heightAnchor.constraint(equalToConstant: 44),

            // Button sizes
            takePictureButton.widthAnchor.constraint(equalToConstant: 70),
            takePictureButton.heightAnchor.constraint(equalToConstant: 70),
            flipCameraButton.widthAnchor.constraint(equalToConstant: 50),
            flipCameraButton.heightAnchor.constraint(equalToConstant: 50),
            doneButton.widthAnchor.constraint(equalToConstant: 50),
            doneButton.heightAnchor.constraint(equalToConstant: 50),
            zoomInButton.widthAnchor.constraint(equalToConstant: 44),
            zoomInButton.heightAnchor.constraint(equalToConstant: 44),
            zoomOutButton.widthAnchor.constraint(equalToConstant: 44),
            zoomOutButton.heightAnchor.constraint(equalToConstant: 44),
            zoomFactorLabel.widthAnchor.constraint(equalToConstant: 50),
            zoomFactorLabel.heightAnchor.constraint(equalToConstant: 25)
        ]

        portraitConstraints.append(contentsOf: commonConstraints)

        // Portrait-specific constraints
        let portraitSpecificConstraints = [
            // Bottom bar - horizontal at bottom
            bottomBarView.heightAnchor.constraint(equalToConstant: 100),
            bottomBarView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor),
            bottomBarView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor),
            bottomBarView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor),

            // Preview view bottom connects to bottom bar top
            previewView.bottomAnchor.constraint(equalTo: bottomBarView.topAnchor),

            // Thumbnail collection view
            thumbnailCollectionView.heightAnchor.constraint(equalToConstant: 80),
            thumbnailCollectionView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 10),
            thumbnailCollectionView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -10),
            thumbnailCollectionView.bottomAnchor.constraint(equalTo: bottomBarView.topAnchor, constant: -90), // Positioned higher to be above zoom controls

            // Take picture button
            takePictureButton.centerXAnchor.constraint(equalTo: bottomBarView.centerXAnchor),
            takePictureButton.centerYAnchor.constraint(equalTo: bottomBarView.centerYAnchor),

            // Flip camera button
            flipCameraButton.leadingAnchor.constraint(equalTo: bottomBarView.leadingAnchor, constant: 30),
            flipCameraButton.centerYAnchor.constraint(equalTo: bottomBarView.centerYAnchor),

            // Done button
            doneButton.trailingAnchor.constraint(equalTo: bottomBarView.trailingAnchor, constant: -30),
            doneButton.centerYAnchor.constraint(equalTo: bottomBarView.centerYAnchor),

            // Zoom buttons - positioned below the film strip
            zoomInButton.bottomAnchor.constraint(equalTo: bottomBarView.topAnchor, constant: -20),
            zoomInButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),

            zoomOutButton.bottomAnchor.constraint(equalTo: bottomBarView.topAnchor, constant: -20),
            zoomOutButton.trailingAnchor.constraint(equalTo: zoomInButton.leadingAnchor, constant: -10),

            // Zoom factor label
            zoomFactorLabel.centerYAnchor.constraint(equalTo: zoomInButton.centerYAnchor),
            zoomFactorLabel.trailingAnchor.constraint(equalTo: zoomOutButton.leadingAnchor, constant: -10)
        ]

        portraitConstraints.append(contentsOf: portraitSpecificConstraints)
    }

    private func setupLandscapeConstraints() {
        // Clear any existing constraints
        landscapeConstraints.removeAll()

        // Add common constraints that don't change with orientation
        let commonConstraints = [
            // Preview view top, leading
            previewView.topAnchor.constraint(equalTo: view.topAnchor),
            previewView.leadingAnchor.constraint(equalTo: view.leadingAnchor),

            // Close button
            closeButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 10),
            closeButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            closeButton.widthAnchor.constraint(equalToConstant: 44),
            closeButton.heightAnchor.constraint(equalToConstant: 44),

            // Flash button
            flashButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 10),
            flashButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            flashButton.widthAnchor.constraint(equalToConstant: 44),
            flashButton.heightAnchor.constraint(equalToConstant: 44),

            // Button sizes
            takePictureButton.widthAnchor.constraint(equalToConstant: 70),
            takePictureButton.heightAnchor.constraint(equalToConstant: 70),
            flipCameraButton.widthAnchor.constraint(equalToConstant: 50),
            flipCameraButton.heightAnchor.constraint(equalToConstant: 50),
            doneButton.widthAnchor.constraint(equalToConstant: 50),
            doneButton.heightAnchor.constraint(equalToConstant: 50),
            zoomInButton.widthAnchor.constraint(equalToConstant: 44),
            zoomInButton.heightAnchor.constraint(equalToConstant: 44),
            zoomOutButton.widthAnchor.constraint(equalToConstant: 44),
            zoomOutButton.heightAnchor.constraint(equalToConstant: 44),
            zoomFactorLabel.widthAnchor.constraint(equalToConstant: 50),
            zoomFactorLabel.heightAnchor.constraint(equalToConstant: 25)
        ]

        landscapeConstraints.append(contentsOf: commonConstraints)

        // Landscape-specific constraints
        let landscapeSpecificConstraints = [
            // Bottom bar - vertical on right side
            bottomBarView.widthAnchor.constraint(equalToConstant: 120),
            bottomBarView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            bottomBarView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor),
            bottomBarView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor),

            // Preview view connects to bottom bar on right
            previewView.trailingAnchor.constraint(equalTo: bottomBarView.leadingAnchor),
            previewView.bottomAnchor.constraint(equalTo: view.bottomAnchor),

            // Thumbnail collection view - horizontal at bottom of preview
            thumbnailCollectionView.heightAnchor.constraint(equalToConstant: 80),
            thumbnailCollectionView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 100), // Give space from left edge
            thumbnailCollectionView.trailingAnchor.constraint(equalTo: bottomBarView.leadingAnchor, constant: -10),
            thumbnailCollectionView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -10),

            // Take picture button - centered in the vertical bar
            takePictureButton.centerXAnchor.constraint(equalTo: bottomBarView.centerXAnchor),
            takePictureButton.centerYAnchor.constraint(equalTo: bottomBarView.centerYAnchor),

            // Flip camera button - above the take picture button
            flipCameraButton.centerXAnchor.constraint(equalTo: bottomBarView.centerXAnchor),
            flipCameraButton.bottomAnchor.constraint(equalTo: takePictureButton.topAnchor, constant: -30),

            // Done button - below the take picture button
            doneButton.centerXAnchor.constraint(equalTo: bottomBarView.centerXAnchor),
            doneButton.topAnchor.constraint(equalTo: takePictureButton.bottomAnchor, constant: 30),

            // Zoom buttons - on the right side of the preview
            zoomInButton.trailingAnchor.constraint(equalTo: bottomBarView.leadingAnchor, constant: -20),
            zoomInButton.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -20),

            zoomOutButton.trailingAnchor.constraint(equalTo: zoomInButton.leadingAnchor, constant: -10),
            zoomOutButton.centerYAnchor.constraint(equalTo: zoomInButton.centerYAnchor),

            // Zoom factor label
            zoomFactorLabel.trailingAnchor.constraint(equalTo: zoomOutButton.leadingAnchor, constant: -10),
            zoomFactorLabel.centerYAnchor.constraint(equalTo: zoomInButton.centerYAnchor)
        ]

        landscapeConstraints.append(contentsOf: landscapeSpecificConstraints)
    }

    private func checkPermissions() {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            setupCaptureSession()
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                if granted {
                    DispatchQueue.main.async {
                        self?.setupCaptureSession()
                    }
                } else {
                    DispatchQueue.main.async {
                        self?.showPermissionAlert()
                    }
                }
            }
        default:
            showPermissionAlert()
        }
    }

    private func showPermissionAlert() {
        let alert = UIAlertController(
            title: "Camera Access Required",
            message: "Please allow camera access to use this feature",
            preferredStyle: .alert
        )

        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel) { [weak self] _ in
            self?.delegate?.multiCameraViewControllerDidCancel(self!)
        })

        alert.addAction(UIAlertAction(title: "Settings", style: .default) { _ in
            if let url = URL(string: UIApplication.openSettingsURLString) {
                UIApplication.shared.open(url)
            }
        })

        present(alert, animated: true)
    }

    // MARK: - Camera Setup
    private func setupCaptureSession() {
        captureSession = AVCaptureSession()
        guard let captureSession = captureSession else { return }

        captureSession.beginConfiguration()

        // Set the quality level
        if captureSession.canSetSessionPreset(.photo) {
            captureSession.sessionPreset = .photo
        }

        // Setup camera input
        guard let videoDevice = getCamera() else {
            captureSession.commitConfiguration()
            return
        }

        do {
            let videoInput = try AVCaptureDeviceInput(device: videoDevice)
            if captureSession.canAddInput(videoInput) {
                captureSession.addInput(videoInput)
            } else {
                captureSession.commitConfiguration()
                return
            }
        } catch {
            captureSession.commitConfiguration()
            return
        }

        // Setup photo output
        photoOutput = AVCapturePhotoOutput()
        guard let photoOutput = photoOutput else {
            captureSession.commitConfiguration()
            return
        }

        if captureSession.canAddOutput(photoOutput) {
            photoOutput.isHighResolutionCaptureEnabled = true
            captureSession.addOutput(photoOutput)
        } else {
            captureSession.commitConfiguration()
            return
        }

        captureSession.commitConfiguration()

        // Setup preview layer on main thread to ensure UI updates are synchronized
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }

            self.previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
            guard let previewLayer = self.previewLayer else { return }

            previewLayer.videoGravity = .resizeAspectFill
            previewLayer.frame = self.previewView.bounds
            self.previewView.layer.addSublayer(previewLayer)

            // Make sure the preview layer is properly sized
            self.updatePreviewLayerFrame()
        }

        // Add tap gesture for focus
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))
        previewView.addGestureRecognizer(tapGesture)

        // Get device zoom capabilities
        if let device = getCamera() {
            minZoomFactor = 1.0
            maxZoomFactor = min(device.activeFormat.videoMaxZoomFactor, 10.0) // Limit max zoom to 10x
        }
    }

    private func getCamera() -> AVCaptureDevice? {
        currentCameraPosition = cameraDirection == .front ? .front : .back

        if let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: currentCameraPosition) {
            return device
        }

        // Fallback to any camera if the requested one is not available
        return AVCaptureDevice.default(for: .video)
    }

    private func startCaptureSession() {
        if captureSession?.isRunning == false {
            // Start session on background thread
            DispatchQueue.global(qos: .userInitiated).async { [weak self] in
                self?.captureSession?.startRunning()

                // Update UI on main thread
                DispatchQueue.main.async {
                    self?.updatePreviewLayerFrame()
                }
            }
        }
    }

    private func stopCaptureSession() {
        if captureSession?.isRunning == true {
            captureSession?.stopRunning()
        }
    }

    // MARK: - Zoom Control
    @objc private func handlePinchGesture(_ gesture: UIPinchGestureRecognizer) {
        guard (captureSession?.inputs.first as? AVCaptureDeviceInput)?.device != nil else { return }

        // Get the pinch scale
        let scale = gesture.scale

        switch gesture.state {
        case .began:
            // Store the current zoom factor when the pinch begins
            lastZoomFactor = currentZoomFactor
        case .changed:
            // Calculate new zoom factor
            let newZoomFactor = max(minZoomFactor, min(lastZoomFactor * scale, maxZoomFactor))
            setZoomFactor(newZoomFactor)
        default:
            break
        }
    }

    @objc private func zoomIn() {
        let newZoomFactor = min(currentZoomFactor * 1.25, maxZoomFactor)
        setZoomFactor(newZoomFactor)
    }

    @objc private func zoomOut() {
        let newZoomFactor = max(currentZoomFactor / 1.25, minZoomFactor)
        setZoomFactor(newZoomFactor)
    }

    private func setZoomFactor(_ zoomFactor: CGFloat) {
        guard let device = (captureSession?.inputs.first as? AVCaptureDeviceInput)?.device else { return }

        do {
            try device.lockForConfiguration()

            // Set the zoom factor
            device.videoZoomFactor = zoomFactor
            currentZoomFactor = zoomFactor

            // Update the zoom factor label
            DispatchQueue.main.async { [weak self] in
                self?.zoomFactorLabel.text = String(format: "%.1fx", zoomFactor)
            }

            device.unlockForConfiguration()
        } catch {
            CAPLog.print("Could not set zoom factor: \(error.localizedDescription)")
        }
    }

    // MARK: - Actions
    @objc private func takePicture() {
        guard let photoOutput = photoOutput else { return }

        // Add loading thumbnail immediately for responsive UI
        addLoadingThumbnail()

        // Configure photo settings
        let photoSettings = AVCapturePhotoSettings()
        photoSettings.flashMode = flashMode

        if let photoPreviewType = photoSettings.availablePreviewPhotoPixelFormatTypes.first {
            photoSettings.previewPhotoFormat = [kCVPixelBufferPixelFormatTypeKey as String: photoPreviewType]
        }

        // Capture the photo
        photoOutput.capturePhoto(with: photoSettings, delegate: self)

        // Provide haptic feedback
        let generator = UIImpactFeedbackGenerator(style: .medium)
        generator.prepare()
        generator.impactOccurred()

        // Play shutter sound
        AudioServicesPlaySystemSound(1108) // Camera shutter sound
    }

    @objc private func flipCamera() {
        guard let captureSession = captureSession else { return }

        // Remove existing input
        captureSession.beginConfiguration()
        if let currentInput = captureSession.inputs.first as? AVCaptureDeviceInput {
            captureSession.removeInput(currentInput)
        }

        // Toggle camera position
        currentCameraPosition = (currentCameraPosition == .back) ? .front : .back

        // Add new input
        guard let videoDevice = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: currentCameraPosition) else {
            captureSession.commitConfiguration()
            return
        }

        do {
            let videoInput = try AVCaptureDeviceInput(device: videoDevice)
            if captureSession.canAddInput(videoInput) {
                captureSession.addInput(videoInput)
            }
        } catch {
            captureSession.commitConfiguration()
            return
        }

        captureSession.commitConfiguration()

        // Keep flash button visible for both cameras (front camera uses screen flash)
        flashButton.isHidden = false

        // Reset zoom when switching cameras
        currentZoomFactor = 1.0
        zoomFactorLabel.text = "1.0x"

        // Update zoom limits for the new camera
        maxZoomFactor = min(videoDevice.activeFormat.videoMaxZoomFactor, 10.0)
    }

    @objc private func toggleFlash() {
        switch flashMode {
        case .auto:
            flashMode = .on
            flashButton.setImage(UIImage(systemName: "bolt.fill"), for: .normal)
        case .on:
            flashMode = .off
            flashButton.setImage(UIImage(systemName: "bolt.slash"), for: .normal)
        case .off:
            flashMode = .auto
            flashButton.setImage(UIImage(systemName: "bolt.badge.a"), for: .normal)
        @unknown default:
            flashMode = .auto
            flashButton.setImage(UIImage(systemName: "bolt.badge.a"), for: .normal)
        }
    }

    @objc private func handleTap(_ gesture: UITapGestureRecognizer) {
        let touchPoint = gesture.location(in: previewView)
        focusAtPoint(touchPoint)
    }

    private func focusAtPoint(_ point: CGPoint) {
        guard let device = (captureSession?.inputs.first as? AVCaptureDeviceInput)?.device,
              device.isFocusPointOfInterestSupported,
              device.isFocusModeSupported(.autoFocus) else { return }

        // Convert the touch point to device coordinates
        let focusPoint = previewLayer?.captureDevicePointConverted(fromLayerPoint: point) ?? CGPoint(x: 0.5, y: 0.5)

        do {
            try device.lockForConfiguration()
            device.focusPointOfInterest = focusPoint
            device.focusMode = .autoFocus

            if device.isExposurePointOfInterestSupported && device.isExposureModeSupported(.autoExpose) {
                device.exposurePointOfInterest = focusPoint
                device.exposureMode = .autoExpose
            }

            device.unlockForConfiguration()

            // Show focus indicator
            showFocusIndicator(at: point)
        } catch {
            CAPLog.print("Could not focus at point: \(error.localizedDescription)")
        }
    }

    private func showFocusIndicator(at point: CGPoint) {
        let focusView = UIView(frame: CGRect(x: 0, y: 0, width: 80, height: 80))
        focusView.layer.borderColor = UIColor.white.cgColor
        focusView.layer.borderWidth = 2
        focusView.center = point
        focusView.alpha = 0

        previewView.addSubview(focusView)

        UIView.animate(withDuration: 0.2, animations: {
            focusView.alpha = 1
        }, completion: { _ in
            UIView.animate(withDuration: 0.5, delay: 0.5, options: [], animations: {
                focusView.alpha = 0
            }, completion: { _ in
                focusView.removeFromSuperview()
            })
        })
    }

    @objc private func cancel() {
        // Don't allow canceling while images are processing
        if hasProcessingImages() {
            return
        }
        
        // If we have images, show confirmation alert
        if !capturedImages.isEmpty {
            let alert = UIAlertController(
                title: "Discard Photos?",
                message: "Are you sure you want to discard all photos?",
                preferredStyle: .alert
            )

            alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
            alert.addAction(UIAlertAction(title: "Discard", style: .destructive) { [weak self] _ in
                guard let self = self else { return }
                self.delegate?.multiCameraViewControllerDidCancel(self)
            })

            present(alert, animated: true)
        } else {
            delegate?.multiCameraViewControllerDidCancel(self)
        }
    }

    @objc private func done() {
        // Check if any images are still processing
        if hasProcessingImages() {
            showProcessingOverlay()
            waitForProcessingCompletion()
        } else {
            finishWithImages()
        }
    }
    
    private func hasProcessingImages() -> Bool {
        return loadingStates.contains(true)
    }
    
    private func showProcessingOverlay() {
        processingOverlay.isHidden = false
        processingSpinner.startAnimating()
        
        // Disable user interaction on the main view
        view.isUserInteractionEnabled = false
        processingOverlay.isUserInteractionEnabled = true
    }
    
    private func hideProcessingOverlay() {
        processingOverlay.isHidden = true
        processingSpinner.stopAnimating()
        
        // Re-enable user interaction
        view.isUserInteractionEnabled = true
    }
    
    private func waitForProcessingCompletion() {
        // Check every 0.1 seconds if processing is complete
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
            guard let self = self else { return }
            
            if self.hasProcessingImages() {
                // Still processing, check again
                self.waitForProcessingCompletion()
            } else {
                // All images processed, hide overlay and finish
                self.hideProcessingOverlay()
                self.finishWithImages()
            }
        }
    }
    
    private func waitForProcessingCompletionThenFinish() {
        // Check every 0.1 seconds if processing is complete
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
            guard let self = self else { return }
            
            if self.hasProcessingImages() {
                // Still processing, check again
                self.waitForProcessingCompletionThenFinish()
            } else {
                // All images processed, hide overlay and finish
                self.hideProcessingOverlay()
                self.finishWithImages()
            }
        }
    }
    
    private func finishWithImages() {
        delegate?.multiCameraViewController(self, didFinishWith: capturedImages, metadata: capturedMetadata)
    }

    // MARK: - Image Management
    private func addLoadingThumbnail() {
        // Add placeholder image and loading state
        capturedImages.append(UIImage()) // Placeholder
        capturedMetadata.append([:])
        loadingStates.append(true)

        // Show the done button once we have at least one image (even loading)
        if doneButton.isHidden {
            doneButton.isHidden = false
        }

        // Update the collection view
        thumbnailCollectionView.reloadData()

        // Scroll to the new image
        let indexPath = IndexPath(item: capturedImages.count - 1, section: 0)
        thumbnailCollectionView.scrollToItem(at: indexPath, at: .right, animated: true)
    }

    private func addCapturedImage(_ image: UIImage, metadata: [String: Any]) {
        // Find the most recent loading thumbnail and replace it
        for i in (0..<loadingStates.count).reversed() {
            if loadingStates[i] {
                capturedImages[i] = image
                capturedMetadata[i] = metadata
                loadingStates[i] = false

                // Update just this cell
                let indexPath = IndexPath(item: i, section: 0)
                thumbnailCollectionView.reloadItems(at: [indexPath])
                break
            }
        }

        // Check if we've reached the maximum number of images
        if maxImages > 0 && capturedImages.count >= maxImages {
            // Wait for processing if needed, then automatically finish
            if hasProcessingImages() {
                showProcessingOverlay()
                waitForProcessingCompletionThenFinish()
            } else {
                finishWithImages()
            }
            return
        }
    }

    private func removeImage(at index: Int) {
        guard index < capturedImages.count else { return }

        capturedImages.remove(at: index)
        capturedMetadata.remove(at: index)
        loadingStates.remove(at: index)

        // Hide the done button if we have no images
        if capturedImages.isEmpty {
            doneButton.isHidden = true
        }

        // Update the collection view
        thumbnailCollectionView.reloadData()
    }
}

// MARK: - AVCapturePhotoCaptureDelegate
extension MultiCameraViewController: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        if let error = error {
            CAPLog.print("Error capturing photo: \(error.localizedDescription)")
            return
        }

        guard let imageData = photo.fileDataRepresentation(),
              let image = UIImage(data: imageData) else {
            return
        }

        // Extract metadata
        let metadata = photo.metadata

        // Fix image orientation based on device orientation
        let fixedImage = fixImageOrientation(image)

        // Add the captured image
        DispatchQueue.main.async { [weak self] in
            self?.addCapturedImage(fixedImage, metadata: metadata)
        }
    }

    private func fixImageOrientation(_ image: UIImage) -> UIImage {
        // First, normalize the image orientation using reformat()
        let normalizedImage = image.reformat()

        // Then apply rotation based on the current device orientation
        let orientation = UIDevice.current.orientation
        let isUsingFrontCamera = currentCameraPosition == .front

        // Create a new CGImage from the normalized image
        guard let cgImage = normalizedImage.cgImage else { return normalizedImage }

        // Determine the correct orientation based on device orientation
        var uiOrientation: UIImage.Orientation = .up // Default is no additional rotation

        switch orientation {
        case .portrait:
            // Portrait is already correct after normalization
            return normalizedImage
        case .portraitUpsideDown:
            // Need 180-degree rotation
            uiOrientation = .down
        case .landscapeLeft:
            // Need counter-clockwise 90-degree rotation for back camera
            uiOrientation = isUsingFrontCamera ? .downMirrored : .left
        case .landscapeRight:
            // Need clockwise 90-degree rotation for back camera
            uiOrientation = isUsingFrontCamera ? .upMirrored : .right
        default:
            // For unknown orientations, use the normalized image
            return normalizedImage
        }

        // Create a new UIImage with the correct orientation
        return UIImage(cgImage: cgImage, scale: normalizedImage.scale, orientation: uiOrientation)
    }
}

// MARK: - UICollectionViewDataSource
extension MultiCameraViewController: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return capturedImages.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ThumbnailCell", for: indexPath) as? ThumbnailCell else {
            return UICollectionViewCell()
        }

        // Check if this thumbnail is still loading
        if indexPath.item < loadingStates.count && loadingStates[indexPath.item] {
            cell.configureAsLoading()
            cell.deleteHandler = nil // Don't allow deletion of loading thumbnails
        } else {
            cell.configure(with: capturedImages[indexPath.item])
            cell.deleteHandler = { [weak self] in
                self?.removeImage(at: indexPath.item)
            }
        }

        return cell
    }
}

// MARK: - UICollectionViewDelegate
extension MultiCameraViewController: UICollectionViewDelegate {
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        // Don't allow interaction with loading thumbnails
        guard indexPath.item < loadingStates.count && !loadingStates[indexPath.item] else {
            return
        }

        // Filter out any loading images for the preview
        let nonLoadingImages = capturedImages.enumerated().compactMap { index, image in
            loadingStates.indices.contains(index) && !loadingStates[index] ? image : nil
        }

        // Calculate the correct starting index in the filtered array
        let nonLoadingIndices = loadingStates.enumerated().compactMap { index, isLoading in
            !isLoading ? index : nil
        }

        guard let startingIndex = nonLoadingIndices.firstIndex(of: indexPath.item) else {
            return
        }

        // Create a custom image preview controller with all non-loading images
        let previewController = ImagePreviewViewController(images: nonLoadingImages, startingIndex: startingIndex)

        // Present it modally
        present(previewController, animated: true)
    }
}