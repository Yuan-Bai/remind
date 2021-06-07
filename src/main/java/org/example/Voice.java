package org.example;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Voice {
    Media media;
    MediaPlayer mp;
    public void interact_talk(String voice_name){
        media = new Media(getVoicePath(voice_name));
        if (mp!=null)mp.dispose();
        mp = new MediaPlayer(media);
        mp.play();
    }
    public String getVoicePath(String imageName){
        return this.getClass().getClassLoader().getResource("voice/"+App.characterName+"_voice/"+App.characterName+"_"+imageName+".mp3").toString();
    }
}
