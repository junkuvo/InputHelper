package junkuvo.apps.inputhelper.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import io.reactivex.Observable;
import junkuvo.apps.inputhelper.OverlayActivity;

public class IntentUtil {

    public enum RequestCode{
        VOICE_RECOGNIZER(0),
        ;
        int code;

        RequestCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static RequestCode getParam(int code){
            return Observable.fromArray(values())
                    .filter(requestCode -> requestCode.code == code)
                    .blockingFirst();
        }
    }

    public static void startOverlayActivity(Context context) {
        Intent intent = new Intent(context, OverlayActivity.class);
        context.startActivity(intent);
    }

    public static void startVoiceRecognizer(Activity context, RecognitionListener recognitionListener,@Nullable View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if (hasVoiceRecognizer(context, intent)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
            SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(context);
            recognizer.setRecognitionListener(recognitionListener);
            recognizer.startListening(intent);
            context.startActivityForResult(intent, RequestCode.VOICE_RECOGNIZER.getCode());
//        startActivityForResult()
        }else {
            if (view != null) {
                Snackbar.make(view, "お客様の端末は音声をテキストに変換する機能に対応していないようです…", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public static boolean hasVoiceRecognizer(Context context, Intent intent){
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    public static void startWeb(Context context, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
