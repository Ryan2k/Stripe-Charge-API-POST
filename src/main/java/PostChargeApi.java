import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.Request;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpConnectTimeoutException;
import java.util.Scanner;

public class PostChargeApi {
    public static void main(String[] args) {
        String jsonPath = "C:\\Users\\ryan2\\OneDrive\\Documents\\Interviews\\Stripe\\Practice Content\\Stripe_Charge_API\\src\\test\\request.json";
        RequestObject req = getRequest(jsonPath);
        System.out.println(req.getKey());

        System.out.println(getResposeString(req));
    }

    public static RequestObject getRequest(String jsonPath){
        RequestObject req = new RequestObject();
        try{
            File file = new File(jsonPath);
            Scanner scan = new Scanner(file);
            StringBuilder builder = new StringBuilder();

            while(scan.hasNextLine()){
                builder.append(scan.nextLine());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            req = mapper.readValue(builder.toString(), RequestObject.class);
            return req;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return req;
    }

    public static String getResposeString(RequestObject req){
        String res = "";
        try{
            URL url = new URL(req.getUrl());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();


            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", req.getKey());
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");

            //we need to write the payload as a JSON
            ObjectMapper mapper = new ObjectMapper();
            String stringJSON = mapper.writeValueAsString(req.getCharge());
            System.out.println(stringJSON);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(mapper.writeValueAsString(req.getCharge()));
            writer.close();

            System.out.println("status: " + connection.getResponseCode());

            //only read if i get a 200

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null){
                System.out.println(line);
                builder.append(line);
            }

            return builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
