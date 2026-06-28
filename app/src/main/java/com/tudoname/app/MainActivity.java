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

    private AnimatorSet floatSet;

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
        setupRadioGroups();
        setupButton();

        // Esperar a que el layout esté completamente medido antes de animar
        cardContent.post(() -> {
            // Pivot en el pin superior-centro (como transform-origin: top center en CSS)
            cardContent.setPivotX(cardContent.getWidth() / 2f);
            cardContent.setPivotY(0f);
            startEntranceAnimations();
        });
    }

    private void initViews() {
        cardContent  = findViewById(R.id.cardContent);
        tvTitle      = findViewById(R.id.tvTitle);
        tvTagline    = findViewById(R.id.tvTagline);
        rgBebés      = findViewById(R.id.rgBebes);
        rgMascotas   = findViewById(R.id.rgMascotas);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // Ocultar todo inicialmente
        cardContent.setAlpha(0f);
        tvTitle.setAlpha(0f);
        tvTagline.setAlpha(0f);
    }

    private void startEntranceAnimations() {

        // ── 1. TÍTULO: popIn rebotado ──
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

        // ── 2. TAGLINE: fadeUp delay 500ms ──
        tvTagline.setTranslationY(24f);
        tvTagline.animate()
            .alpha(1f).translationY(0f)
            .setDuration(700)
            .setStartDelay(500)
            .setInterpolator(new DecelerateInterpolator(1.5f))
            .start();

        // ── 3. HOJA: entra desde arriba con rotación y rebote ──
        cardContent.setTranslationY(-40f);
        cardContent.setRotation(-6f);
        cardContent.setScaleX(0.92f);
        cardContent.setScaleY(0.92f);

        AnimatorSet hojaEntrada = new AnimatorSet();
        hojaEntrada.playTogether(
            ObjectAnimator.ofFloat(cardContent, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(cardContent, "translationY", -40f, 0f),
            ObjectAnimator.ofFloat(cardContent, "rotation", -6f, 0f),
            ObjectAnimator.ofFloat(cardContent, "scaleX", 0.92f, 1f),
            ObjectAnimator.ofFloat(cardContent, "scaleY", 0.92f, 1f)
        );
        hojaEntrada.setDuration(750);
        hojaEntrada.setStartDelay(200);
        hojaEntrada.setInterpolator(new OvershootInterpolator(1.4f));
        hojaEntrada.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                // Recalcular pivot después de la entrada (el tamaño ya es definitivo)
                cardContent.setPivotX(cardContent.getWidth() / 2f);
                cardContent.setPivotY(0f);
                startHojaFlotando();
                startBtnPulse();
            }
        });
        hojaEntrada.start();

        // ── 4. Contenido interno: fadeUp escalonado ──
        animateFadeUpChildren();
    }

    private void startTitleShimmer() {
        // Pulso sutil de opacidad que simula el shimmer del logo
        ObjectAnimator shimmer = ObjectAnimator.ofFloat(tvTitle, "alpha", 1f, 0.82f, 1f);
        shimmer.setDuration(2800);
        shimmer.setRepeatCount(ValueAnimator.INFINITE);
        shimmer.setRepeatMode(ValueAnimator.RESTART);
        shimmer.setInterpolator(new AccelerateDecelerateInterpolator());
        shimmer.start();
    }

    /**
     * Balanceo continuo del papel colgado de un chinche.
     * Replica exacta del CSS:
     *   0%→rotate(0)  20%→rotate(0.8deg)  40%→rotate(-0.5deg)
     *   60%→rotate(0.6deg)  80%→rotate(-0.4deg)  100%→rotate(0)
     * pivot = top center  →  setPivotY(0)
     */
    private void startHojaFlotando() {
        // Rotación: balanceo de papel colgado
        ObjectAnimator swingRot = ObjectAnimator.ofFloat(
            cardContent, "rotation",
            0f, 0.9f, -0.6f, 0.7f, -0.45f, 0f
        );
        swingRot.setDuration(5000);
        swingRot.setRepeatCount(ValueAnimator.INFINITE);
        swingRot.setRepeatMode(ValueAnimator.RESTART);
        swingRot.setInterpolator(new AccelerateDecelerateInterpolator());

        // Flotado vertical leve que acompaña el balanceo
        ObjectAnimator swingY = ObjectAnimator.ofFloat(
            cardContent, "translationY",
            0f, 2.5f, 1f, 2f, 0.5f, 0f
        );
        swingY.setDuration(5000);
        swingY.setRepeatCount(ValueAnimator.INFINITE);
        swingY.setRepeatMode(ValueAnimator.RESTART);
        swingY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Sombra oscilante (simula hojaSombra del CSS)
        ObjectAnimator swingElev = ObjectAnimator.ofFloat(
            cardContent, "elevation",
            12f, 22f, 10f, 20f, 11f, 12f
        );
        swingElev.setDuration(5000);
        swingElev.setRepeatCount(ValueAnimator.INFINITE);
        swingElev.setRepeatMode(ValueAnimator.RESTART);
        swingElev.setInterpolator(new AccelerateDecelerateInterpolator());

        floatSet = new AnimatorSet();
        floatSet.playTogether(swingRot, swingY, swingElev);
        floatSet.start();
    }

    private void startBtnPulse() {
        // Pulso suave del botón: escala y elevación
        ObjectAnimator px = ObjectAnimator.ofFloat(btnSiguiente, "scaleX", 1f, 1.03f, 1f);
        ObjectAnimator py = ObjectAnimator.ofFloat(btnSiguiente, "scaleY", 1f, 1.03f, 1f);
        ObjectAnimator pe = ObjectAnimator.ofFloat(btnSiguiente, "elevation", 6f, 16f, 6f);
        for (ObjectAnimator a : new ObjectAnimator[]{px, py, pe}) {
            a.setDuration(2800);
            a.setRepeatCount(ValueAnimator.INFINITE);
            a.setRepeatMode(ValueAnimator.RESTART);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        AnimatorSet btnSet = new AnimatorSet();
        btnSet.playTogether(px, py, pe);
        btnSet.start();
    }

    private void animateFadeUpChildren() {
        int[] ids = { R.id.rgBebes, R.id.rgMascotas, R.id.btnSiguiente };
        int[] delays = { 650, 780, 900 };
        for (int i = 0; i < ids.length; i++) {
            View v = findViewById(ids[i]);
            if (v == null) continue;
            v.setAlpha(0f);
            v.setTranslationY(28f);
            v.animate()
                .alpha(1f).translationY(0f)
                .setDuration(520)
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
            .translationY(-5f)
            .scaleX(1.08f)
            .scaleY(1.08f)
            .setDuration(220)
            .setInterpolator(new OvershootInterpolator(1.6f))
            .start();
    }

    private void setupButton() {
        btnSiguiente.setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
                .withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(220)
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
                0f, -16f, 16f, -12f, 12f, -7f, 7f, -3f, 3f, 0f
            );
            shaker.setDuration(520);
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
        if (floatSet != null) floatSet.cancel();
    }
}
