package com.tudoname.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    private RadioGroup rgBebes, rgMascotas;
    private FrameLayout cardContent;
    private TextView tvTitle, tvTagline;
    private Button btnSiguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Ocultar todo antes de animar
        tvTitle.setAlpha(0f);
        tvTagline.setAlpha(0f);
        cardContent.setAlpha(0f);

        // Esperar a que el layout esté completamente medido
        cardContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cardContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cardContent.setPivotX(cardContent.getWidth() / 2f);
                cardContent.setPivotY(0f);
                animarTitulo();
                animarHoja();
            }
        });

        setupRadioGroups();
        setupButton();
    }

    private void animarTitulo() {
        tvTitle.setTranslationY(-40f);
        tvTitle.setScaleX(0.7f);
        tvTitle.setScaleY(0.7f);
        tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(650)
            .setInterpolator(new OvershootInterpolator(1.8f))
            .withEndAction(() -> {
                ObjectAnimator shimmer = ObjectAnimator.ofFloat(tvTitle, "alpha", 1f, 0.75f, 1f);
                shimmer.setDuration(2800);
                shimmer.setRepeatCount(ValueAnimator.INFINITE);
                shimmer.setRepeatMode(ValueAnimator.RESTART);
                shimmer.setInterpolator(new AccelerateDecelerateInterpolator());
                shimmer.start();
            })
            .start();

        tvTagline.setTranslationY(24f);
        tvTagline.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(500)
            .setInterpolator(new DecelerateInterpolator(1.5f))
            .start();
    }

    private void animarHoja() {
        cardContent.setTranslationY(-50f);
        cardContent.setRotation(-8f);
        cardContent.setScaleX(0.85f);
        cardContent.setScaleY(0.85f);

        cardContent.animate()
            .alpha(1f)
            .translationY(0f)
            .rotation(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(750)
            .setStartDelay(200)
            .setInterpolator(new OvershootInterpolator(1.6f))
            .withEndAction(() -> {
                cardContent.setPivotX(cardContent.getWidth() / 2f);
                cardContent.setPivotY(0f);
                arrancarBalanceo();
                arrancarPulsoBoton();
            })
            .start();

        new Handler(Looper.getMainLooper()).postDelayed(this::animarContenidoHoja, 700);
    }

    private void arrancarBalanceo() {
        ObjectAnimator rot = ObjectAnimator.ofFloat(cardContent, "rotation", 0f, 0.9f, -0.6f, 0.7f, -0.4f, 0f);
        rot.setDuration(5500);
        rot.setRepeatCount(ValueAnimator.INFINITE);
        rot.setRepeatMode(ValueAnimator.RESTART);
        rot.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator transY = ObjectAnimator.ofFloat(cardContent, "translationY", 0f, 4f, 1f, 3f, 0.5f, 0f);
        transY.setDuration(5500);
        transY.setRepeatCount(ValueAnimator.INFINITE);
        transY.setRepeatMode(ValueAnimator.RESTART);
        transY.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(rot, transY);
        set.start();
    }

    private void arrancarPulsoBoton() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(btnSiguiente, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(btnSiguiente, "scaleY", 1f, 1.05f, 1f);
        for (ObjectAnimator a : new ObjectAnimator[]{scaleX, scaleY}) {
            a.setDuration(2200);
            a.setRepeatCount(ValueAnimator.INFINITE);
            a.setRepeatMode(ValueAnimator.RESTART);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        AnimatorSet pulso = new AnimatorSet();
        pulso.playTogether(scaleX, scaleY);
        pulso.start();
    }

    private void animarContenidoHoja() {
        int[] ids    = {R.id.tvSectionLabel, R.id.rgBebes, R.id.rgMascotas, R.id.btnSiguiente};
        int[] delays = {0, 80, 160, 240};
        for (int i = 0; i < ids.length; i++) {
            android.view.View v = findViewById(ids[i]);
            if (v == null) continue;
            v.setAlpha(0f);
            v.setTranslationY(30f);
            v.animate()
                .alpha(1f).translationY(0f)
                .setDuration(500)
                .setStartDelay(delays[i])
                .setInterpolator(new DecelerateInterpolator(1.5f))
                .start();
        }
    }

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
            .scaleX(1.15f).scaleY(1.15f).translationY(-6f)
            .setDuration(150).setInterpolator(new OvershootInterpolator(2.5f))
            .withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).translationY(0f)
                    .setDuration(220).setInterpolator(new OvershootInterpolator(2f)).start())
            .start();
    }

    private void setupButton() {
        btnSiguiente.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                .withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(220)
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
            ObjectAnimator shaker = ObjectAnimator.ofFloat(cardContent, "translationX",
                0f, -18f, 18f, -12f, 12f, -6f, 6f, 0f);
            shaker.setDuration(480);
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
