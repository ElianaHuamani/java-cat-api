import com.google.gson.Gson;
import com.squareup.okhttp.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GatosService {
    /**
     * GET a cat randomly, then shows it.
     * */
    public static void verGatos() throws IOException {
        // 1. vamos a traer los datos del API  [okhttp]
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/images/search")
                .get().build();
        Response response = client.newCall(request).execute();

        // 2. cortar los corchetes [gson]
        String elJson=response.body().string();
        elJson= elJson.substring(1,elJson.length());
        elJson= elJson.substring(0,elJson.length()-1);

        // 3. crear un obj json
        Gson gson = new Gson();
        Gato gato = gson.fromJson(elJson, Gato.class);

        try {
                // 4. redimensionar en caso de necesitar
                Image image = null;
                URL url = new URL(gato.getUrl());

                //old code
                /*image = ImageIO.read(url);
                ImageIcon fondoGato = new ImageIcon(image);*/

                //new code - Works!
                  HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                  httpcon.addRequestProperty("User-Agent", "");
                  BufferedImage bufferedImage = ImageIO.read(httpcon.getInputStream());
                  ImageIcon fondoGato = new ImageIcon(bufferedImage);

                //redimensionamos
                //if (fondoGato.getIconWidth() > 800) {
                    //redimensionamos
                    Image fondo = fondoGato.getImage();
                    fondoGato = new ImageIcon(fondo.getScaledInstance(800, 600, Image.SCALE_SMOOTH));
                //}

                //creamos menus
                String menu = "Opciones : \n" +
                        " 1. Ver otra imagen \n" +
                        " 2. Favorito \n" +
                        " 3. Volver \n";
                String[] botones = {"Ver otra imagen","Favorito","Volver"};
                String id_gato = gato.getId();
                String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato, JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[0]);
                int seleccion = -1;

                //validamos que opcion selecciona el usuario
                for (int i = 0; i < botones.length; i++) {
                    if (opcion.equals(botones[i])) {
                        seleccion = i;
                    }
                }
                switch (seleccion) {
                    case 0:
                        verGatos();
                        break;
                    case 1:
                        favoritoGato(gato);
                        break;
                    default:
                        break;
                }
        }catch (IOException e){
            System.out.println(e);
        }
    }

    /**
     * GET all one's favourites cats, then shows one randomly.
     * */
    public static void verFavoritos(String apiKey) throws IOException {
        try{
            //1. Calling API - GET
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites")
                    .method("GET", null)
                    .addHeader("x-api-key", apiKey)
                    .build();
            Response response = client.newCall(request).execute();

            //2. JSON Formatting
            String elJson=response.body().string();
            Gson gson = new Gson();
            //Gato gato = gson.fromJson(elJson, Gato.class);
            GatoFavorito[] gatosArray=gson.fromJson(elJson,GatoFavorito[].class);

            if (gatosArray.length>0){
                int min = 1;
                int max = gatosArray.length;
                int aleatorio=(int) (Math.random()*((max-min)+1)) + min;
                int indice=aleatorio-1;
                GatoFavorito gatoFav= gatosArray[indice]; //solo se mostrara un gato aleatoriamente de todos los gatos favoritos.

                try {
                    // 4. redimensionar en caso de necesitar
                    Image image = null;
                    URL url = new URL(gatoFav.image.getUrl());

                    //new code - Works!
                    HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                    httpcon.addRequestProperty("User-Agent", "");
                    BufferedImage bufferedImage = ImageIO.read(httpcon.getInputStream());
                    ImageIcon fondoGato = new ImageIcon(bufferedImage);

                    //redimensionamos
                    //if (fondoGato.getIconWidth() > 800) {
                        //redimensionamos
                        Image fondo = fondoGato.getImage();
                        fondoGato = new ImageIcon(fondo.getScaledInstance(800, 600, Image.SCALE_SMOOTH));
                    //}

                    //creamos menus
                    String menu = "Opciones : \n" +
                            " 1. ver otra imagen \n" +
                            " 2. Eliminar favorito \n" +
                            " 3. volver \n";
                    String[] botones = {"ver otra imagen", "eliminar favorito", "volver"};
                    String id_gato = gatoFav.getId();
                    String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato, JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[0]);
                    int seleccion = -1;

                    //validamos que opcion selecciona el usuario
                    for (int i = 0; i < botones.length; i++) {
                        if (opcion.equals(botones[i])) {
                            seleccion = i;
                        }
                    }

                    switch (seleccion) {
                        case 0:
                            verFavoritos(apiKey);
                            break;
                        case 1:
                            borrarFavorito(gatoFav);
                            break;
                        default:
                            break;
                    }
                }catch (IOException e){
                    System.out.println(e);
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }
    }
    /**
     * POST a cat as a favourite.
     * */
    public static void favoritoGato(Gato gato){
         try {
             OkHttpClient client = new OkHttpClient();
             MediaType mediaType = MediaType.parse("application/json,text/plain");
             RequestBody body = RequestBody.create(mediaType, "{\n    \"image_id\": \""+gato.getId()+"\"\n}");
             Request request = new Request.Builder()
                     .url("https://api.thecatapi.com/v1/favourites")
                     .method("POST", body)
                     .addHeader("Content-Type", "application/json")
                     .addHeader("x-api-key", gato.getApiKey())
                     .build();
             Response response = client.newCall(request).execute();
         }catch (IOException e){
             System.out.println(e);
         }
    }

    /**
     * DELETE a selected cat.
     * */
    public static void borrarFavorito(GatoFavorito gatoFavorito) throws IOException {
        try{
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites/"+gatoFavorito.getId())
                    .method("DELETE", body)
                    .addHeader("x-api-key", gatoFavorito.getApiKey())
                    .build();
            Response response = client.newCall(request).execute();
        }catch (IOException e){
            System.out.println(e);
        }
    }


}
