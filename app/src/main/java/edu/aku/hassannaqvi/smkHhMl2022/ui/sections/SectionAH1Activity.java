package edu.aku.hassannaqvi.smkHhMl2022.ui.sections;

import static edu.aku.hassannaqvi.smkHhMl2022.core.MainApp.familyList;
import static edu.aku.hassannaqvi.smkHhMl2022.core.MainApp.selectedAdol;
import static edu.aku.hassannaqvi.smkHhMl2022.core.MainApp.selectedMWRA;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import edu.aku.hassannaqvi.smkHhMl2022.databinding.ActivitySectionAh1Binding;
import edu.aku.hassannaqvi.smkHhMl2022.ui.EndingActivity;


public class SectionAH1Activity extends AppCompatActivity {

    private static final String TAG = "SectionAH1Activity";
    ActivitySectionAh1Binding bi;
    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MainApp.langRTL ? R.style.AppThemeSindhi : R.style.AppThemeEnglish1);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_section_ah1);
        setSupportActionBar(bi.toolbar);
        db = MainApp.appInfo.dbHelper;
        bi.sno.setText(familyList.get(Integer.parseInt(selectedAdol.isEmpty() ? selectedMWRA : selectedAdol) - 1).getD101());
        bi.name.setText(familyList.get(Integer.parseInt(selectedAdol.isEmpty() ? selectedMWRA : selectedAdol) - 1).getD102());
        bi.index.setText(familyList.get(Integer.parseInt(selectedAdol.isEmpty() ? selectedMWRA : selectedAdol) - 1).getIndexed());

        try {
            MainApp.adol = db.getAdolByUUid();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "JSONException(Adolescent): " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        bi.setForm(MainApp.adol);
    }

    private boolean insertNewRecord() {
        if (!MainApp.adol.getUid().equals("") || MainApp.superuser) return true;
        MainApp.adol.populateMeta();
        long rowId = 0;
        try {
            rowId = db.addAdolescent(MainApp.adol);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.db_excp_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        MainApp.adol.setId(String.valueOf(rowId));
        if (rowId > 0) {
            MainApp.adol.setUid(MainApp.adol.getDeviceId() + MainApp.adol.getId());
            db.updatesAdolColumn(TableContracts.AdolescentTable.COLUMN_UID, MainApp.adol.getUid());
            return true;
        } else {
            Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private boolean updateDB() {
        if (MainApp.superuser) return true;
        int updcount = 0;
        try {
            updcount = db.updatesAdolColumn(TableContracts.AdolescentTable.COLUMN_SAH1, MainApp.adol.sAH1toString());
        } catch (JSONException e) {
            Toast.makeText(this, R.string.upd_db + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (updcount == 1) {
            return true;
        } else {
            Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void btnContinue(View view) {
        bi.llbtn.setEnabled(false);
        new Handler().postDelayed(() -> bi.llbtn.setEnabled(true), 5000);
        if (!formValidation()) return;
        if (MainApp.adol.getUid().equals("") ? insertNewRecord() : updateDB()) {
            finish();
            startActivity(new Intent(this, SectionAH2Activity.class));
        } else {
            Toast.makeText(this, R.string.fail_db_upd, Toast.LENGTH_SHORT).show();
        }
    }


    public void btnEnd(View view) {
        finish();
        startActivity(new Intent(this, EndingActivity.class).putExtra("complete", false));
    }


    private boolean formValidation() {
        return Validator.emptyCheckingContainer(this, bi.GrpName);
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApp.lockScreen(this);
    }
}