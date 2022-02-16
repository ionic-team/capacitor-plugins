package com.capacitorjs.plugins.camera;

public class CameraSettings {

    public static final int DEFAULT_QUALITY = 90;
    public static final boolean DEFAULT_SAVE_IMAGE_TO_GALLERY = false;
    public static final boolean DEFAULT_CORRECT_ORIENTATION = true;

    private CameraResultType resultType = CameraResultType.BASE64;
    private int quality = DEFAULT_QUALITY;
    private boolean shouldResize = false;
    private boolean shouldCorrectOrientation = DEFAULT_CORRECT_ORIENTATION;
    private boolean saveToGallery = DEFAULT_SAVE_IMAGE_TO_GALLERY;
    private boolean allowEditing = false;
    private int width = 0;
    private int height = 0;
    private CameraSource source = CameraSource.PROMPT;

    // FarmQA begin
    private boolean saveToDataDirectory = false;
    private String resultFilename;
    private boolean createThumbnail = false;
    private String thumbnailFilename;
    private int thumbnailWidth = 70;
    private int thumbnailHeight = 70;
    // FarmQA end

    public CameraResultType getResultType() {
        return resultType;
    }

    public void setResultType(CameraResultType resultType) {
        this.resultType = resultType;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public boolean isShouldResize() {
        return shouldResize;
    }

    public void setShouldResize(boolean shouldResize) {
        this.shouldResize = shouldResize;
    }

    public boolean isShouldCorrectOrientation() {
        return shouldCorrectOrientation;
    }

    public void setShouldCorrectOrientation(boolean shouldCorrectOrientation) {
        this.shouldCorrectOrientation = shouldCorrectOrientation;
    }

    public boolean isSaveToGallery() {
        return saveToGallery;
    }

    public void setSaveToGallery(boolean saveToGallery) {
        this.saveToGallery = saveToGallery;
    }

    public boolean isAllowEditing() {
        return allowEditing;
    }

    public void setAllowEditing(boolean allowEditing) {
        this.allowEditing = allowEditing;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public CameraSource getSource() {
        return source;
    }

    public void setSource(CameraSource source) {
        this.source = source;
    }

    // FarmQA begin
    public boolean getSaveToDataDirectory() {
        return getResultType() == CameraResultType.URI && this.saveToDataDirectory;
    }

    public void setSaveToDataDirectory(boolean saveToDataDirectory) { this.saveToDataDirectory = saveToDataDirectory; }

    public String getResultFilename() {
        return this.resultFilename;
    }

    public void setResultFilename(String filename) {
        this.resultFilename = filename;
    }

    public boolean getCreateThumbnail() {
        return this.createThumbnail && (this.thumbnailHeight > 0 || this.thumbnailWidth > 0);
    }

    public void setCreateThumbnail(boolean createThumbnail) { this.createThumbnail = createThumbnail; }

    public String getThumbnailFilename() {
        return this.thumbnailFilename;
    }

    public void setThumbnailFilename(String filename) {
        this.thumbnailFilename = filename;
    }

    public int getThumbnailWidth() {
        return this.thumbnailWidth;
    }

    public void setThumbnailWidth(int width) {
        this.thumbnailWidth = width;
    }

    public int getThumbnailHeight() {
        return this.thumbnailHeight;
    }

    public void setThumbnailHeight(int height) {
        this.thumbnailHeight = height;
    }
    // FarmQA end
}
