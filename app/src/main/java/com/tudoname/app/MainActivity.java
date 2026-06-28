package com.tudoname.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rgBebés, rgMascotas;
    private FrameLayout cardContent;   // ahora FrameLayout, no CardView
    private TextView tvTitle, tvTagline;
    private Button btnSiguiente;

    // Animadores del balanceo — guardados para cancelarlos si es necesario
    private ObjectAnimator swingRotation;
    private ObjectAnimator swingTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Pivot en la parte superior centro — como papel colgado de un chinche
        cardContent.post(() -> {
            cardContent.setPivotX(cardContent.getWidth() / 2f);
            cardContent.setPivotY(0f);
        });
    }

    private void startEntranceAnimations() {
        // ── Título: pop-in desde arriba con rebote ──
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

        // ── Tagline: fade-up suave ──
        tvTagline.setAlpha(0f);
        tvTagline.setTranslationY(20f);
        tvTagline.animate()
            .alpha(1f).translationY(0f)
            .setDuration(600).setStartDelay(400)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // ── Hoja: entrada desde arriba con rotación y rebote (igual que web) ──
        cardContent.setAlpha(0f);
        cardContent.setTranslationY(-30f);
        cardContent.setRotation(-6f);
        cardContent.setScaleX(0.92f);
        cardContent.setScaleY(0.92f);

        AnimatorSet hojaEntrada = new AnimatorSet();
        hojaEntrada.playTogether(
            ObjectAnimator.ofFloat(cardContent, "alpha", 0f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(cardContent, "translationY", -30f, 0f).setDuration(700),
            ObjectAnimator.ofFloat(cardContent, "rotation", -6f, 0f).setDuration(700),
            ObjectAnimator.ofFloat(cardContent, "scaleX", 0.92f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(cardContent, "scaleY", 0.92f, 1f).setDuration(700)
        );
        hojaEntrada.setStartDelay(200);
        hojaEntrada.setInterpolator(new OvershootInterpolator(1.5f));
        hojaEntrada.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Inicia el balanceo continuo DESPUÉS de que termina la entrada
                startHojaFlotando();
            }
        });
        hojaEntrada.start();
    }

    /**
     * Replica el keyframe hojaFlotando de la web:
     *   0%   rotate(0)     translateY(0)
     *   20%  rotate(0.8)   translateY(1)
     *   40%  rotate(-0.5)  translateY(0.5)
     *   60%  rotate(0.6)   translateY(1)
     *   80%  rotate(-0.4)  translateY(0)
     *   100% rotate(0)     translateY(0)
     * Duración: 5000ms  |  Infinito
     */
    private void startHojaFlotando() {
        // Rotación — simula balanceo de papel colgado del chinche
        swingRotation = ObjectAnimator.ofFloat(
            cardContent, "rotation",
            0f, 0.8f, -0.5f, 0.6f, -0.4f, 0f
        );
        swingRotation.setDuration(5000);
        swingRotation.setRepeatCount(ValueAnimator.INFINITE);
        swingRotation.setRepeatMode(ValueAnimator.RESTART);
        swingRotation.setInterpolator(new AccelerateDecelerateInterpolator());

        // Traslación vertical suave
        swingTranslation = ObjectAnimator.ofFloat(
            cardContent, "translationY",
            0f, 1f, 0.5f, 1f, 0f, 0f
        );
        swingTranslation.setDuration(5000);
        swingTranslation.setRepeatCount(ValueAnimator.INFINITE);
        swingTranslation.setRepeatMode(ValueAnimator.RESTART);
        swingTranslation.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet floatSet = new AnimatorSet();
        floatSet.playTogether(swingRotation, swingTranslation);
        floatSet.start();
    }

    private void setupRadioGroups() {
        rgBebés.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) rgMascotas.clearCheck();
        });
        rgMascotas.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) rgBebés.clearCheck();
        });
    }

    private void setupButton() {
        btnSiguiente.setOnClickListener(v -> {
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
            // Shake shake
            ObjectAnimator shaker = ObjectAnimator.ofFloat(
                cardContent, "translationX",
                0f, -12f, 12f, -8f, 8f, -4f, 4f, 0f
            );
            shaker.setDuration(400);
            shaker.start();
            return;
        }

        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra("tipo", tipo);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (swingRotation != null) swingRotation.cancel();
        if (swingTranslation != null) swingTranslation.cancel();
    }
}
