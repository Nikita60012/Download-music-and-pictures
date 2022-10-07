import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    private static final String IN_FILE_TXT = "src\\materials\\inFile.txt";
    private static final String IN_PICTURE_FILE_TXT = "src\\materials\\inPictureFile.txt";
    private static final String OUT_FILE_TXT = "src\\materials\\outFile.txt";
    private static final String OUT_PICTURE_FILE_TXT = "src\\materials\\outPictureFile.txt";
    private static final String PATH_TO_MUSIC = "src\\materials\\music\\music";
    private static final String PATH_TO_PICTURE = "src\\materials\\picture\\picture";

    public static void main(String[] args) {
        String Url;
        String picUrl;

        new File("src/materials/music").mkdirs();
        new File("src/materials/picture").mkdirs();

        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
             BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_TXT));
             BufferedWriter outPictureFile = new BufferedWriter(new FileWriter(OUT_PICTURE_FILE_TXT));
             BufferedReader inPicFile = new BufferedReader(new FileReader(IN_PICTURE_FILE_TXT));) {
            while ((Url = inFile.readLine()) != null && (picUrl = inPicFile.readLine()) != null) {
                URL url = new URL(Url);
                URL urlPic = new URL(picUrl);

                String result;
                String pictureSite;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                     BufferedReader bf = new BufferedReader(new InputStreamReader(urlPic.openStream()))) {
                    result = bufferedReader.lines().collect(Collectors.joining("\n"));
                    pictureSite = bf.lines().collect(Collectors.joining("\n"));
                }
                Pattern email_pattern = Pattern.compile("/track/dl/\\d{8}/grottesque-\\S*.mp3");
                Pattern picture_pattern = Pattern.compile("images/screenshots/p\\d{2}.png");
                Matcher matcher = email_pattern.matcher(result);
                Matcher matcherPic = picture_pattern.matcher(pictureSite);
                int i = 0;
                while(matcherPic.find()){
                    outPictureFile.write( "http://www.celestegame.com/" + matcherPic.group() + "\r\n");
                }
                while (matcher.find() && i < 9) {
                    outFile.write("https://musify.club" + matcher.group() + "\r\n");
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader musicFile = new BufferedReader(new FileReader(OUT_FILE_TXT));
             BufferedReader pictureFile = new BufferedReader(new FileReader(OUT_PICTURE_FILE_TXT))) {
            String music;
            String picture;
            int count = 1;
            try {
                while ((music = musicFile.readLine()) != null)  {
                    downloadUsingNIO(music, PATH_TO_MUSIC + String.valueOf(count) + ".mp3");
                    count++;
                }
                count = 1;
                while((picture = pictureFile.readLine()) != null){
                    downloadUsingNIO(picture, PATH_TO_PICTURE + String.valueOf(count) + ".png");
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
        FileOutputStream stream = new FileOutputStream(file);
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        stream.close();
        byteChannel.close();
    }
}
