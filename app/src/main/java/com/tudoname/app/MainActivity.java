package com.tudoname.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
    private FrameLayout cardContent;
    private TextView tvTitle, tvTagline;
    private Button btnSiguiente;

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

        // Pivot en el pin (top center) — igual que transform-origin: top center
        // ViewTreeObserver garantiza que width/height ya están medidos
        cardContent.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            cardContent.setPivotX(cardContent.getWidth() / 2f);
            cardContent.setPivotY(0f);
        });
    }

    private void startEntranceAnimations() {

        // ── 1. TÍTULO: popIn — scale(0.7) + translateY(-20) → normal ──
        tvTitle.setAlpha(0f);
        tvTitle.setScaleX(0.7f);
        tvTitle.setScaleY(0.7f);
        tvTitle.setTranslationY(-20f);

        AnimatorSet titleAnim = new AnimatorSet();
        titleAnim.playTogether(
            ObjectAnimator.ofFloat(tvTitle, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(tvTitle, "scaleX", 0.7f, 1f),
            ObjectAnimator.ofFloat(tvTitle, "scaleY", 0.7f, 1f),
            ObjectAnimator.ofFloat(tvTitle, "translationY", -20f, 0f)
        );
        titleAnim.setDuration(600);
        titleAnim.setInterpolator(new OvershootInterpolator(1.6f));
        titleAnim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator a) {
                startTitleShimmer();
            }
        });
        titleAnim.start();

        // ── 2. TAGLINE: fadeUp — delay 500ms ──
        tvTagline.setAlpha(0f);
        tvTagline.setTranslationY(24f);
        tvTagline.animate()
            .alpha(1f).translationY(0f)
            .setDuration(700)
            .setStartDelay(500)
            .setInterpolator(new DecelerateInterpolator(1.5f))
            .start();

        // ── 3. HOJA: hojaEntrada ──
        cardContent.setAlpha(0f);
        cardContent.setTranslationY(-30f);
        cardContent.setRotation(-6f);
        cardContent.setScaleX(0.92f);
        cardContent.setScaleY(0.92f);

        AnimatorSet hojaEntrada = new AnimatorSet();
        hojaEntrada.playTogether(
            ObjectAnimator.ofFloat(cardContent, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(cardContent, "translationY", -30f, 0f),
            ObjectAnimator.ofFloat(cardContent, "rotation", -6f, 0f),
            ObjectAnimator.ofFloat(cardContent, "scaleX", 0.92f, 1f),
            ObjectAnimator.ofFloat(cardContent, "scaleY", 0.92f, 1f)
        );
        hojaEntrada.setDuration(700);
        hojaEntrada.setStartDelay(200);
        hojaEntrada.setInterpolator(new OvershootInterpolator(1.4f));
        hojaEntrada.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                startHojaFlotando();
                startBtnGradMove();
            }
        });
        hojaEntrada.start();

        // ── 4. Contenido interno: fadeUp escalonado ──
        animateFadeUpChildren();
    }

    private void startTitleShimmer() {
        ObjectAnimator shimmer = ObjectAnimator.ofFloat(tvTitle, "alpha", 1f, 0.85f, 1f);
        shimmer.setDuration(2800);
        shimmer.setRepeatCount(ValueAnimator.INFINITE);
        shimmer.setRepeatMode(ValueAnimator.RESTART);
        shimmer.setInterpolator(new AccelerateDecelerateInterpolator());
        shimmer.start();
    }

    /**
     * hojaFlotando: replica exacta del CSS de la web
     * 0→0.8°→-0.5°→0.6°→-0.4°→0° | 5000ms infinito
     * pivot = top center (como transform-origin: top center)
     */
    private void startHojaFlotando() {
        // Asegurar pivot correcto antes de animar
        cardContent.setPivotX(cardContent.getWidth() / 2f);
        cardContent.setPivotY(0f);

        // Rotación: replica keyframes del CSS hojaFlotando
        swingRotation = ObjectAnimator.ofFloat(
            cardContent, "rotation",
            0f, 0.8f, -0.5f, 0.6f, -0.4f, 0f
        );
        swingRotation.setDuration(5000);
        swingRotation.setRepeatCount(ValueAnimator.INFINITE);
        swingRotation.setRepeatMode(ValueAnimator.RESTART);
        swingRotation.setInterpolator(new AccelerateDecelerateInterpolator());

        // TranslationY: leve flotado vertical acompañando el balanceo
        swingTranslation = ObjectAnimator.ofFloat(
            cardContent, "translationY",
            0f, 2f, 1f, 2f, 0f, 0f
        );
        swingTranslation.setDuration(5000);
        swingTranslation.setRepeatCount(ValueAnimator.INFINITE);
        swingTranslation.setRepeatMode(ValueAnimator.RESTART);
        swingTranslation.setInterpolator(new AccelerateDecelerateInterpolator());

        // Elevación oscilante (simula hojaSombra del CSS)
        ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(
            cardContent, "elevation",
            12f, 20f, 10f, 18f, 11f, 12f
        );
        shadowAnim.setDuration(5000);
        shadowAnim.setRepeatCount(ValueAnimator.INFINITE);
        shadowAnim.setRepeatMode(ValueAnimator.RESTART);
        shadowAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet floatSet = new AnimatorSet();
        floatSet.playTogether(swingRotation, swingTranslation, shadowAnim);
        floatSet.start();
    }

    private void startBtnGradMove() {
        ObjectAnimator btnPulse = ObjectAnimator.ofFloat(btnSiguiente, "scaleX", 1f, 1.02f, 1f);
        btnPulse.setDuration(3000);
        btnPulse.setRepeatCount(ValueAnimator.INFINITE);
        btnPulse.setRepeatMode(ValueAnimator.RESTART);
        btnPulse.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator btnPulseY = ObjectAnimator.ofFloat(btnSiguiente, "scaleY", 1f, 1.02f, 1f);
        btnPulseY.setDuration(3000);
        btnPulseY.setRepeatCount(ValueAnimator.INFINITE);
        btnPulseY.setRepeatMode(ValueAnimator.RESTART);
        btnPulseY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator btnGlow = ObjectAnimator.ofFloat(btnSiguiente, "elevation", 8f, 16f, 8f);
        btnGlow.setDuration(3000);
        btnGlow.setRepeatCount(ValueAnimator.INFINITE);
        btnGlow.setRepeatMode(ValueAnimator.RESTART);
        btnGlow.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet btnSet = new AnimatorSet();
        btnSet.playTogether(btnPulse, btnPulseY, btnGlow);
        btnSet.start();
    }

    private void animateFadeUpChildren() {
        int[] ids = { R.id.rgBebes, R.id.rgMascotas, R.id.btnSiguiente };
        int[] delays = { 600, 700, 800 };
        for (int i = 0; i < ids.length; i++) {
            View v = findViewById(ids[i]);
            if (v == null) continue;
            v.setAlpha(0f);
            v.setTranslationY(24f);
            v.animate()
                .alpha(1f).translationY(0f)
                .setDuration(500)
                .setStartDelay(delays[i])
                .setInterpolator(new DecelerateInterpolator(1.5f))
                .start();
        }
    }

    private void setupRadioGroups() {
        rgBebés.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                rgMascotas.clearCheck();
                animateSelected(findViewById(checkedId));
            }
        });
        rgMascotas.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                rgBebés.clearCheck();
                animateSelected(findViewById(checkedId));
            }
        });
    }

    private void animateSelected(View v) {
        if (v == null) return;
        v.animate()
            .translationY(-4f)
            .scaleX(1.06f)
            .scaleY(1.06f)
            .setDuration(220)
            .setInterpolator(new OvershootInterpolator(1.4f))
            .start();
    }

    private void setupButton() {
        btnSiguiente.setOnClickListener(v -> {
            v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80)
                .withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .withEndAction(this::goToResultados)
                        .start()
                ).start();
        });
    }

    private void goToResultados() {
        String tipo = getSeleccion();
        if (tipo == null) {
            Toast.makeText(this, "Por favor selecciona una opción ✨", Toast.LENGTH_SHORT).show();
            ObjectAnimator shaker = ObjectAnimator.ofFloat(
                cardContent, "translationX",
                0f, -14f, 14f, -10f, 10f, -6f, 6f, -3f, 3f, 0f
            );
            shaker.setDuration(500);
            shaker.setInterpolator(new DecelerateInterpolator());
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
        if (bebId != -1) return ((RadioButton) findViewById(bebId)).getText().toString();
        int masId = rgMascotas.getCheckedRadioButtonId();
        if (masId != -1) return ((RadioButton) findViewById(masId)).getText().toString();
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (swingRotation != null) swingRotation.cancel();
        if (swingTranslation != null) swingTranslation.cancel();
    }
}
