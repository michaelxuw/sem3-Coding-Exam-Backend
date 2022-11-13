package dtos;

public class apiPictures {

    private String url;
    private String image;
    private String file;

    public apiPictures(String url, String image, String file) {
        this.url = url;
        this.image = image;
        this.file = file;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public String getFile() {
        return file;
    }
}
