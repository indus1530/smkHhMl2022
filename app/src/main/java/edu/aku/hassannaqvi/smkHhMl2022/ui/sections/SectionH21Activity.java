package edu.aku.hassannaqvi.smkHhMl2022.ui.sections;

import static edu.aku.hassannaqvi.smkHhMl2022.core.MainApp.form;
import static edu.aku.hassannaqvi.smkHhMl2022.core.MainApp.mwra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.validatorcrawler.aliazaz.Validator;

import org.json.JSONException;

import edu.aku.hassannaqvi.smkHhMl2022.R;
import edu.aku.hassannaqvi.smkHhMl2022.contracts.TableContracts;
import edu.aku.hassannaqvi.smkHhMl2022.core.MainApp;
import edu.aku.hassannaqvi.smkHhMl2022.database.DatabaseHelper;
import edu.aku.hassannaqvi.smkHhMl2022.databinding.ActivitySectionH21Binding;

public class SectionH21Activity extends AppCompatActivity {


    private static final String TAG = "SectionH21Activity";
    ActivitySectionH21Binding bi;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MainApp.langRTL ? R.style.AppThemeUrdu : R.style.AppThemeEnglish1);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_section_h21);
        setSupportActionBar(bi.toolbar);
        db = MainApp.appInfo.dbHelper;
        bi.setForm(form);
    }


    private boolean updateDB() {
        if (MainApp.superuser) return true;

        db = MainApp.appInfo.getDbHelper();
        long updcount = 0;
        try {
            updcount = db.updatesFormColumn(TableContracts.MwraTable.COLUMN_SH2, mwra.sH2toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, R.string.upd_db + e.getMessage());
            Toast.makeText(this, R.string.upd_db + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (updcount > 0) return true;
        else {
            Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        //return true;
    }

    public void btnContinue(View view) {
        if (!formValidation()) return;
        // if (!insertNewRecord()) return;
        // saveDraft();
        if (updateDB()) {
            setResult(RESULT_OK);
            Intent i;
            i = new Intent(this, SectionH22Activity.class).putExtra("complete", true).setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, R.string.fail_db_upd, Toast.LENGTH_SHORT).show();
        }
    }


    public void btnEnd(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean formValidation() {
        return Validator.emptyCheckingContainer(this, bi.GrpName);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back Press Not Allowed", Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED);
        finish();
    }


}