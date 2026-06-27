package com.tudoname.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ResultadosActivity extends AppCompatActivity {

    private String tipo;
    private List<String> nombres;
    private int currentIndex = 0;

    // ── Banco de nombres ──────────────────────────────────────────────────────
    private static final Map<String, List<String>> NOMBRES = new HashMap<>();
    static {
        NOMBRES.put("Niño", Arrays.asList(
            "Mateo","Santiago","Sebastián","Nicolás","Alejandro",
            "Emilio","Rafael","Lucas","Joaquín","Andrés",
            "Diego","Carlos","Miguel","Tomás","Pablo",
            "Javier","Ignacio","Fernando","Eduardo","Ricardo",
            "Gabriel","Rodrigo","Marco","Álvaro","Hugo"
        ));
        NOMBRES.put("Niña", Arrays.asList(
            "Valentina","Sofía","Isabella","Camila","Valeria",
            "Luciana","Gabriela","Mariana","Daniela","Natalia",
            "Fernanda","Catalina","Paola","Claudia","Renata",
            "Andrea","Alejandra","Jimena","Paula","Adriana",
            "María","Ana","Laura","Elena","Ximena"
        ));
        NOMBRES.put("Perro", Arrays.asList(
            "Max","Buddy","Charlie","Rocky","Cooper",
            "Duke","Bear","Toby","Tucker","Zeus",
            "Simba","Rex","Milo","Jack","Oscar",
            "Thor","Coco","Bruno","Apollo","Loki"
        ));
        NOMBRES.put("Perra", Arrays.asList(
            "Luna","Bella","Daisy","Lucy","Lola",
            "Sophie","Molly","Nala","Cleo","Rosie",
            "Mia","Chloe","Stella","Gracie","Ruby",
            "Penny","Maggie","Ellie","Ginger","Zoe"
        ));
        NOMBRES.put("Gato", Arrays.asList(
            "Oliver","Leo","Mochi","Loki","Simba",
            "Shadow","Tiger","Felix","Oscar","Whiskers",
            "Jasper","Apollo","Oreo","Pepper","Chester",
            "Thor","Merlin","Cosmo","Bruno","Max"
        ));
        NOMBRES.put("Gata", Arrays.asList(
            "Luna","Nala","Cleo","Mimi","Kitty",
            "Bella","Stella","Zara","Mochi","Sasha",
            "Aurora","Misty","Chloe","Willow","Perla",
            "Violet","Mia","Lily","Sable","Nyx"
        ));
    }

    private TextView tvTipo, tvNombre, tvNumero;
    private CardView cardNombre;
    private Button btnAnterior, btnSiguiente, btnRandom;
    private View btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootResultados), (v, insets) -> {
            var sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        tipo = getIntent().getStringExtra("tipo");
        nombres = new ArrayList<>(NOMBRES.getOrDefault(tipo, Arrays.asList("Sin nombres")));
        java.util.Collections.shuffle(nombres, new Random());

        initViews();
        setupBackNavigation();
        setupClicks();
        displayNombre();
        entranceAnim();
    }

    private void initViews() {
        tvTipo      = findViewById(R.id.tvTipo);
        tvNombre    = findViewById(R.id.tvNombre);
        tvNumero    = findViewById(R.id.tvNumero);
        cardNombre  = findViewById(R.id.cardNombre);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente= findViewById(R.id.btnSiguiente);
        btnRandom   = findViewById(R.id.btnRandom);
        btnVolver   = findViewById(R.id.btnVolver);

        String emoji = getEmojiForTipo(tipo);
        tvTipo.setText(emoji + " " + tipo);
    }

    private void setupBackNavigation() {
        // Modern back press handling — replaces deprecated onBackPressed()
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });
    }

    private void navigateBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private String getEmojiForTipo(String t) {
        return switch (t) {
            case "Niño"  -> "👶";
            case "Niña"  -> "👶";
            case "Perro" -> "🐶";
            case "Perra" -> "🐶";
            case "Gato"  -> "🐱";
            case "Gata"  -> "🐱";
            default      -> "✨";
        };
    }

    private void setupClicks() {
        btnSiguiente.setOnClickListener(v -> {
            if (currentIndex < nombres.size() - 1) {
                currentIndex++;
                animateCardSwipe(true);
            }
        });

        btnAnterior.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                animateCardSwipe(false);
            }
        });

        btnRandom.setOnClickListener(v -> {
            int prev = currentIndex;
            while (currentIndex == prev && nombres.size() > 1) {
                currentIndex = new Random().nextInt(nombres.size());
            }
            animateCardSwipe(true);
        });

        btnVolver.setOnClickListener(v -> navigateBack());
    }

    private void displayNombre() {
        tvNombre.setText(nombres.get(currentIndex));
        tvNumero.setText((currentIndex + 1) + " / " + nombres.size());
        btnAnterior.setEnabled(currentIndex > 0);
        btnSiguiente.setEnabled(currentIndex < nombres.size() - 1);
        btnAnterior.setAlpha(currentIndex > 0 ? 1f : 0.4f);
        btnSiguiente.setAlpha(currentIndex < nombres.size() - 1 ? 1f : 0.4f);
    }

    private void animateCardSwipe(boolean goRight) {
        float outX = goRight ? -800f : 800f;
        float inX  = goRight ?  800f : -800f;

        ObjectAnimator out = ObjectAnimator.ofFloat(cardNombre, "translationX", 0f, outX);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(cardNombre, "alpha", 1f, 0f);
        AnimatorSet animOut = new AnimatorSet();
        animOut.playTogether(out, fadeOut);
        animOut.setDuration(180);
        animOut.withEndAction(() -> {
            displayNombre();
            cardNombre.setTranslationX(inX);
            cardNombre.setAlpha(0f);
            ObjectAnimator in = ObjectAnimator.ofFloat(cardNombre, "translationX", inX, 0f);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(cardNombre, "alpha", 0f, 1f);
            AnimatorSet animIn = new AnimatorSet();
            animIn.playTogether(in, fadeIn);
            animIn.setDuration(250);
            animIn.setInterpolator(new OvershootInterpolator(1.2f));
            animIn.start();
        });
        animOut.start();
    }

    private void entranceAnim() {
        cardNombre.setAlpha(0f);
        cardNombre.setTranslationY(40f);
        cardNombre.animate()
            .alpha(1f).translationY(0f)
            .setDuration(600).setStartDelay(200)
            .setInterpolator(new OvershootInterpolator(1.3f))
            .start();

        // Floating loop
        ObjectAnimator floatY = ObjectAnimator.ofFloat(cardNombre, "translationY", 0f, 5f, 0f);
        floatY.setDuration(3500);
        floatY.setRepeatCount(ValueAnimator.INFINITE);
        floatY.setInterpolator(new AccelerateDecelerateInterpolator());
        floatY.setStartDelay(900);
        floatY.start();
    }
}
