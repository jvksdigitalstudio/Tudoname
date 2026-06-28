package com.tudoname.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    private RadioGroup rgBebes, rgMascotas;
    private FrameLayout cardContent;
    private TextView tvTitle, tvTagline;
    private Button btnSiguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        cardContent  = findViewById(R.id.cardContent);
        tvTitle      = findViewById(R.id.tvTitle);
        tvTagline    = findViewById(R.id.tvTagline);
        rgBebes      = findViewById(R.id.rgBebes);
        rgMascotas   = findViewById(R.id.rgMascotas);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // Ocultar antes de animar
        tvTitle.setAlpha(0f);
        tvTagline.setAlpha(0f);
        cardContent.setAlpha(0f);

        cardContent.post(() -> {
            cardContent.setPivotX(cardContent.getWidth() / 2f);
            cardContent.setPivotY(0f);
            animarTitulo();
            animarHoja();
        });

        setupRadioGroups();
        setupButton();
    }

    // ── Animación título: popIn + shimmer loop ──────────────────────────────
    private void animarTitulo() {
        tvTitle.setTranslationY(-30f);
        tvTitle.setScaleX(0.7f);
        tvTitle.setScaleY(0.7f);
        tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setInterpolator(new OvershootInterpolator(1.8f))
            .withEndAction(() -> {
                // Shimmer: pulso de opacidad infinito
                ObjectAnimator shimmer = ObjectAnimator.ofFloat(tvTitle, "alpha", 1f, 0.78f, 1f);
                shimmer.setDuration(2800);
                shimmer.setRepeatCount(ValueAnimator.INFINITE);
                shimmer.setRepeatMode(ValueAnimator.RESTART);
                shimmer.setInterpolator(new AccelerateDecelerateInterpolator());
                shimmer.start();
            })
            .start();

        // Tagline: fadeUp con delay
        tvTagline.setTranslationY(20f);
        tvTagline.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(450)
            .setInterpolator(new DecelerateInterpolator(1.5f))
            .start();
    }

    // ── Animación hoja: entrada rotada + balanceo colgante infinito ──────────
    private void animarHoja() {
        cardContent.setTranslationY(-40f);
        cardContent.setRotation(-6f);
        cardContent.setScaleX(0.88f);
        cardContent.setScaleY(0.88f);

        cardContent.animate()
            .alpha(1f)
            .translationY(0f)
            .rotation(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(700)
            .setStartDelay(200)
            .setInterpolator(new OvershootInterpolator(1.4f))
            .withEndAction(() -> {
                cardContent.setPivotX(cardContent.getWidth() / 2f);
                cardContent.setPivotY(0f);
                arrancarBalanceo();
                arrancarPulsBoton();
            })
            .start();

        new Handler(Looper.getMainLooper()).postDelayed(this::animarContenido, 650);
    }

    // ── Balanceo continuo: papel colgado del chinche ─────────────────────────
    private void arrancarBalanceo() {
        ObjectAnimator rot = ObjectAnimator.ofFloat(cardContent, "rotation",
            0f, 0.8f, -0.5f, 0.6f, -0.3f, 0f);
        rot.setDuration(5000);
        rot.setRepeatCount(ValueAnimator.INFINITE);
        rot.setRepeatMode(ValueAnimator.RESTART);
        rot.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator transY = ObjectAnimator.ofFloat(cardContent, "translationY",
            0f, 3f, 1f, 2.5f, 0.5f, 0f);
        transY.setDuration(5000);
        transY.setRepeatCount(ValueAnimator.INFINITE);
        transY.setRepeatMode(ValueAnimator.RESTART);
        transY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator elev = ObjectAnimator.ofFloat(cardContent, "elevation",
            12f, 22f, 10f, 20f, 11f, 12f);
        elev.setDuration(5000);
        elev.setRepeatCount(ValueAnimator.INFINITE);
        elev.setRepeatMode(ValueAnimator.RESTART);
        elev.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(rot, transY, elev);
        set.start();
    }

    // ── Pulso botón Siguiente ─────────────────────────────────────────────────
    private void arrancarPulsBoton() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(btnSiguiente, "scaleX", 1f, 1.04f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(btnSiguiente, "scaleY", 1f, 1.04f, 1f);
        ObjectAnimator elev   = ObjectAnimator.ofFloat(btnSiguiente, "elevation", 8f, 20f, 8f);
        for (ObjectAnimator a : new ObjectAnimator[]{scaleX, scaleY, elev}) {
            a.setDuration(2000);
            a.setRepeatCount(ValueAnimator.INFINITE);
            a.setRepeatMode(ValueAnimator.RESTART);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        AnimatorSet pulso = new AnimatorSet();
        pulso.playTogether(scaleX, scaleY, elev);
        pulso.start();
    }

    // ── Contenido de la hoja aparece escalonado ───────────────────────────────
    private void animarContenido() {
        int[] ids    = {R.id.rgBebes, R.id.rgMascotas, R.id.btnSiguiente};
        int[] delays = {0, 100, 200};
        for (int i = 0; i < ids.length; i++) {
            android.view.View v = findViewById(ids[i]);
            if (v == null) continue;
            v.setAlpha(0f);
            v.setTranslationY(28f);
            v.animate()
                .alpha(1f).translationY(0f)
                .setDuration(480)
                .setStartDelay(delays[i])
                .setInterpolator(new DecelerateInterpolator(1.5f))
                .start();
        }
    }

    // ── Radio groups mutuamente excluyentes + rebote al seleccionar ───────────
    private void setupRadioGroups() {
        rgBebes.setOnCheckedChangeListener((g, id) -> {
            if (id != -1) {
                rgMascotas.clearCheck();
                rebotarVista(findViewById(id));
            }
        });
        rgMascotas.setOnCheckedChangeListener((g, id) -> {
            if (id != -1) {
                rgBebes.clearCheck();
                rebotarVista(findViewById(id));
            }
        });
    }

    private void rebotarVista(android.view.View v) {
        if (v == null) return;
        v.animate()
            .scaleX(1.12f).scaleY(1.12f).translationY(-5f)
            .setDuration(160).setInterpolator(new OvershootInterpolator(2.2f))
            .withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).translationY(0f)
                    .setDuration(200).setInterpolator(new OvershootInterpolator(1.5f)).start())
            .start();
    }

    // ── Botón Siguiente: press + navegar ─────────────────────────────────────
    private void setupButton() {
        btnSiguiente.setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
                .withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                        .setInterpolator(new OvershootInterpolator(2.5f))
                        .withEndAction(this::irAResultados)
                        .start())
                .start();
        });
    }

    private void irAResultados() {
        String tipo = getSeleccion();
        if (tipo == null) {
            Toast.makeText(this, "Selecciona una opción ✨", Toast.LENGTH_SHORT).show();
            // Shake de la hoja
            ObjectAnimator shaker = ObjectAnimator.ofFloat(cardContent, "translationX",
                0f, -16f, 16f, -10f, 10f, -5f, 5f, 0f);
            shaker.setDuration(450);
            shaker.start();
            return;
        }
        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra("tipo", tipo);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private String getSeleccion() {
        int b = rgBebes.getCheckedRadioButtonId();
        if (b != -1) return ((RadioButton) findViewById(b)).getText().toString();
        int m = rgMascotas.getCheckedRadioButtonId();
        if (m != -1) return ((RadioButton) findViewById(m)).getText().toString();
        return null;
    }
}
