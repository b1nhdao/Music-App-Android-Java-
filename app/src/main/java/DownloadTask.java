//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class DownloadTask extends AsyncTask<Void, Void, Boolean> {
//    private String songUrl, coverUrl, artist, songTitle;
//
//    public DownloadTask(String songUrl, String coverUrl, String artist, String songTitle) {
//        this.songUrl = songUrl;
//        this.coverUrl = coverUrl;
//        this.artist = artist;
//        this.songTitle = songTitle;
//    }
//
//    @Override
//    protected Boolean doInBackground(Void... voids) {
//        try {
//            // Download MP3
//            URL url = new URL(songUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.connect();
//
//            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                return false; // Handle connection error
//            }
//
//            InputStream input = connection.getInputStream();
//            File songFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), songTitle + ".mp3");
//            FileOutputStream output = new FileOutputStream(songFile);
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//
//            while ((bytesRead = input.read(buffer)) != -1) {
//                output.write(buffer, 0, bytesRead);
//            }
//
//            output.close();
//            input.close();
//
//            // Download Cover Image
//            url = new URL(coverUrl);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.connect();
//
//            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                return false;
//            }
//
//            input = connection.getInputStream();
//            File coverFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), songTitle + "_cover.jpg");
//            output = new FileOutputStream(coverFile);
//
//            while ((bytesRead = input.read(buffer)) != -1) {
//                output.write(buffer, 0, bytesRead);
//            }
//
//            output.close();
//            input.close();
//
//            // Add to MediaStore
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Audio.Media.ARTIST, artist);
//            values.put(MediaStore.Audio.Media.TITLE, songTitle);
//            values.put(MediaStore.Audio.Media.DATA, songFile.getAbsolutePath());
//            values.put(MediaStore.Audio.Media.ALBUM, songTitle);
//            values.put(MediaStore.Audio.Media.ALBUM_ARTIST, artist);
//            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
//
//            ContentResolver contentResolver = getContentResolver();
//            contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(Boolean success) {
//        if (success) {
//            Toast.makeText(SongPlayingActivity.this, "Download complete", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(SongPlayingActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
