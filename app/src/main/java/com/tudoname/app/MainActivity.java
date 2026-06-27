package com.tudoname.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rgBebés, rgMascotas;
    private CardView cardContent;
    private TextView tvTitle, tvTagline;
    private Button btnSiguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        startEntranceAnimations();
        setupRadioGroups();
        setupButton();
    }

    private void initViews() {
        cardContent  = findViewById(R.id.cardContent);
        tvTitle      = findViewById(R.id.tvTitle);
        tvTagline    = findViewById(R.id.tvTagline);
        rgBebés      = findViewById(R.id.rgBebes);
        rgMascotas   = findViewById(R.id.rgMascotas);
        btnSiguiente = findViewById(R.id.btnSiguiente);
    }

    private void startEntranceAnimations() {
        // Title pop-in
        tvTitle.setAlpha(0f);
        tvTitle.setScaleX(0.7f);
        tvTitle.setScaleY(0.7f);
        tvTitle.setTranslationY(-30f);

        AnimatorSet titleAnim = new AnimatorSet();
        titleAnim.playTogether(
            ObjectAnimator.ofFloat(tvTitle, "alpha", 0f, 1f).setDuration(500),
            ObjectAnimator.ofFloat(tvTitle, "scaleX", 0.7f, 1f).setDuration(500),
            ObjectAnimator.ofFloat(tvTitle, "scaleY", 0.7f, 1f).setDuration(500),
            ObjectAnimator.ofFloat(tvTitle, "translationY", -30f, 0f).setDuration(500)
        );
        titleAnim.setInterpolator(new OvershootInterpolator(1.2f));
        titleAnim.start();

        // Tagline fade up (delayed)
        tvTagline.setAlpha(0f);
        tvTagline.setTranslationY(20f);
        tvTagline.animate()
            .alpha(1f).translationY(0f)
            .setDuration(600).setStartDelay(400)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // Card entrance from top with bounce
        cardContent.setAlpha(0f);
        cardContent.setTranslationY(-40f);
        cardContent.setRotation(-4f);
        cardContent.setScaleX(0.92f);
        cardContent.setScaleY(0.92f);

        AnimatorSet cardAnim = new AnimatorSet();
        cardAnim.playTogether(
            ObjectAnimator.ofFloat(cardContent, "alpha", 0f, 1f).setDuration(600),
            ObjectAnimator.ofFloat(cardContent, "translationY", -40f, 0f).setDuration(600),
            ObjectAnimator.ofFloat(cardContent, "rotation", -4f, 0f).setDuration(600),
            ObjectAnimator.ofFloat(cardContent, "scaleX", 0.92f, 1f).setDuration(600),
            ObjectAnimator.ofFloat(cardContent, "scaleY", 0.92f, 1f).setDuration(600)
        );
        cardAnim.setStartDelay(200);
        cardAnim.setInterpolator(new OvershootInterpolator(1.4f));
        cardAnim.start();

        // Subtle floating animation loop
        startFloatingAnimation();
    }

    private void startFloatingAnimation() {
        ObjectAnimator floatY = ObjectAnimator.ofFloat(cardContent, "translationY", 0f, 6f, 0f);
        floatY.setDuration(4000);
        floatY.setRepeatCount(ValueAnimator.INFINITE);
        floatY.setInterpolator(new AccelerateDecelerateInterpolator());
        floatY.setStartDelay(800);
        floatY.start();
    }

    private void setupRadioGroups() {
        // Bebes group clears mascotas selection when something is selected
        rgBebés.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                rgMascotas.clearCheck();
            }
        });

        // Mascotas group clears bebes selection when something is selected
        rgMascotas.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                rgBebés.clearCheck();
            }
        });
    }

    private void setupButton() {
        btnSiguiente.setOnClickListener(v -> {
            // Bounce animation on tap
            v.animate().scaleX(0.94f).scaleY(0.94f).setDuration(80)
                .withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .withEndAction(this::goToResultados).start()
                ).start();
        });
    }

    private void goToResultados() {
        String tipo = getSeleccion();
        if (tipo == null) {
            Toast.makeText(this, "Por favor selecciona una opción ✨", Toast.LENGTH_SHORT).show();
            // Shake card
            ObjectAnimator shaker = ObjectAnimator.ofFloat(cardContent, "translationX",
                0f, -12f, 12f, -8f, 8f, -4f, 4f, 0f);
            shaker.setDuration(400);
            shaker.start();
            return;
        }

        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra("tipo", tipo);

        // Slide transition
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private String getSeleccion() {
        int bebId = rgBebés.getCheckedRadioButtonId();
        if (bebId != -1) {
            RadioButton rb = findViewById(bebId);
            return rb.getText().toString();
        }
        int masId = rgMascotas.getCheckedRadioButtonId();
        if (masId != -1) {
            RadioButton rb = findViewById(masId);
            return rb.getText().toString();
        }
        return null;
    }
}
