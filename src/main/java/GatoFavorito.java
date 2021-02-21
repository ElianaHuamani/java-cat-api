public class GatoFavorito {
    String id;
    String image_id;
    String apiKey="a3ace105-5bfd-4b55-83d1-7bb428f99fe1";
    GatoImage image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public GatoImage getImage() {
        return image;
    }

    public void setImage(GatoImage image) {
        this.image = image;
    }
}
