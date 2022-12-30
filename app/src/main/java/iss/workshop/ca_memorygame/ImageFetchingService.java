package iss.workshop.ca_memorygame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class ImageFetchingService {

    public ArrayList<String> imgUrlList = new ArrayList<>();
    public ArrayList<Bitmap> imageContents = new ArrayList<>();
    public ArrayList<File> imageFiles = new ArrayList<>();

    public String prepareImageUrls(String url){
        if(url == null || url == ""){
            return "Please enter valid URL";
        }
        try {
            imgUrlList = new ArrayList<>();
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("img[src]");
            for (Element link : links) {
                if (link.attr("src").contains(".jpg") && link.attr("src").contains("https://")
                        && !link.attr("src").contains("?")) {
                    imgUrlList.add(link.attr("src"));
                    if (imgUrlList.size() == 20)
                        break;
                }
            }
            return "success";
        }
        catch (IOException e)
        {
            return "fail";
        }
        catch (Exception e)
        {
            String message = e.getMessage();
            return e.getMessage();
        }
    }

    public boolean downloadImage(String url,File targetFile)
    {
        try {
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();

            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            in.close();
            imageFiles.add(targetFile);
            imageContents.add(BitmapFactory.decodeFile(targetFile.getAbsolutePath()));
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
