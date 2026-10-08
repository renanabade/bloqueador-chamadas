package app.bloqueador;

import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    static boolean ativo(Context c) {
        return c.getSharedPreferences("p", 0).getBoolean("ativo", true);
    }

    private Button botao;
    private TextView estado, aviso;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.CENTER);
        l.setPadding(64, 64, 64, 64);
        estado = new TextView(this);
        estado.setTextSize(24);
        estado.setTypeface(Typeface.DEFAULT_BOLD);
        estado.setGravity(Gravity.CENTER);
        botao = new Button(this);
        botao.setTextSize(20);
        botao.setTextColor(Color.WHITE);
        botao.setAllCaps(false);
        botao.setOnClickListener(v -> {
            getSharedPreferences("p", 0).edit().putBoolean("ativo", !ativo(this)).apply();
            atualizar();
        });
        aviso = new TextView(this);
        aviso.setGravity(Gravity.CENTER);
        aviso.setTextSize(15);
        aviso.setPadding(0, 48, 0, 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 220);
        lp.topMargin = 48;
        l.addView(estado);
        l.addView(botao, lp);
        l.addView(aviso);
        setContentView(l);
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizar();
        pedirPermissoes();
    }

    private boolean temPapel() {
        RoleManager rm = getSystemService(RoleManager.class);
        return rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
    }

    private boolean temContatos() {
        return checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    private void pedirPermissoes() {
        if (!temContatos()) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        } else if (!temPapel()) {
            Intent i = getSystemService(RoleManager.class).createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
            startActivityForResult(i, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int c, String[] p, int[] r) {
        atualizar();
        pedirPermissoes();
    }

    @Override
    protected void onActivityResult(int c, int r, Intent d) {
        atualizar();
    }

    private void atualizar() {
        boolean on = ativo(this);
        estado.setText(on ? "Bloqueio ligado" : "Bloqueio desligado");
        botao.setText(on ? "Desligar (liberar chamadas)" : "Ligar bloqueio");
        GradientDrawable g = new GradientDrawable();
        g.setCornerRadius(48);
        g.setColor(on ? Color.parseColor("#B3261E") : Color.parseColor("#2E7D32"));
        botao.setBackground(g);
        String s = "";
        if (!temContatos()) s = "Falta permitir o acesso aos contatos.";
        else if (!temPapel()) s = "Falta definir este app como app de identificação de chamadas.";
        else s = on ? "Números fora dos seus contatos são recusados." : "Todas as chamadas tocam normalmente.";
        aviso.setText(s);
    }
}
