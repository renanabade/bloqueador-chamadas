package app.bloqueador;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telecom.Call;
import android.telecom.CallScreeningService;

public class Triagem extends CallScreeningService {
    @Override
    public void onScreenCall(Call.Details d) {
        CallResponse.Builder r = new CallResponse.Builder();
        if (d.getCallDirection() == Call.Details.DIRECTION_INCOMING
                && MainActivity.ativo(this) && !conhecido(this, d)) {
            r.setDisallowCall(true).setRejectCall(true).setSkipNotification(true);
        }
        respondToCall(d, r.build());
    }

    // Sem permissão de contatos ou sem número (oculto): trata como desconhecido.
    private static boolean conhecido(Context c, Call.Details d) {
        if (c.checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED || d.getHandle() == null) return false;
        String n = d.getHandle().getSchemeSpecificPart();
        if (n == null || n.isEmpty()) return false;
        Uri u = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(n));
        try (Cursor q = c.getContentResolver().query(u, new String[]{PhoneLookup._ID}, null, null, null)) {
            return q != null && q.moveToFirst();
        }
    }
}
