package Common;

public class DBR{
    private final boolean isImage;
    private final boolean isError;

    private final String responseText;
    private final String imgUrl;

    public DBR(String responseText, boolean isError) {
        this.isError = isError;
        this.responseText = responseText;
        this.isImage = false;
        imgUrl = "";
    }

    public DBR(String responseText, String imgUrl, boolean isError) {
        this.imgUrl = imgUrl;
        this.isImage = true;

        this.isError = isError;
        this.responseText = responseText;
    }

    public DBR(String responseText) {
        this.responseText = responseText;
        this.isError = false;
        this.isImage = false;
        imgUrl = "";
    }

    public boolean isError() {
        return isError;
    }

    public String getResponseText() {
        return responseText;
    }

    public boolean isImage() {
        return isImage;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
