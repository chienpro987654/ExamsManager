package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AssignmentPointStatistic extends AppCompatActivity {

    PieChart pieChart;
    ListView listView;
    List<ScoreClass> scoreClassList;
    Intent intent;
    String assignID = "assign1";
    String assignName;
    String groupName;
    Button bt_export;
    private File filePath;

    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Group");
    DatabaseReference assignmentReference = FirebaseDatabase.getInstance().getReference("Assignment");

    String classify[] ={"8-10","6-8","4-6","2-4","0-2"};
    int classify_count[] = {0, 0, 0 , 0, 0};
    List<Integer> intScoreList;

    CircularProgressIndicator circularProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_assignment_point_statistic);

        intent = getIntent();
        assignID = intent.getStringExtra("assignID");
        assignName = intent.getStringExtra("assignName");
        groupName = intent.getStringExtra("groupName");

        pieChart =  (PieChart) findViewById(R.id.pie_chart_point_statistic);
        listView = (ListView) findViewById(R.id.lv_point_statistic);
        bt_export = (Button) findViewById(R.id.bt_export_transcript_to_excel);

        scoreClassList = new ArrayList<>();
        intScoreList = new ArrayList<>();
        Personal_Transcript_Adapter adapter = new Personal_Transcript_Adapter(getApplicationContext(),R.layout.personal_transcript_item,scoreClassList);
        listView.setAdapter(adapter);

        assignmentReference.child(assignID).child("transcript").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        if(snapshot.exists())
                        {
                            String scoreID =snapshot.getKey().toString();
                            String score = snapshot.getValue().toString();
                            if(score.matches("Unknown"))
                                break;
                            float scoreFloat = Float.parseFloat(score);
                            if(scoreFloat>=8)
                                classify_count[0]++;
                            else if(scoreFloat>=6)
                                classify_count[1]++;
                            else if(scoreFloat>=4)
                                classify_count[2]++;
                            else if(scoreFloat>=2)
                                classify_count[3]++;
                            else
                                classify_count[4]++;

                            userReference.child(scoreID).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        String userName = snapshot.getValue().toString();
                                        scoreClassList.add(new ScoreClass(userName,score));
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    setupPieChart();
                    Log.i("test classify",String.valueOf(String.valueOf(classify_count[0])));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bt_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                String filename = "/"+assignName.replace(" ","_")+"_"+currentTime+".xls";
                Log.i("Excel file name",filename);

                filePath = new File(Environment.getExternalStorageDirectory() + filename);
                ActivityCompat.requestPermissions(AssignmentPointStatistic.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

                HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                HSSFSheet hssfSheet = hssfWorkbook.createSheet(assignName);

                HSSFFont defaultFont= hssfWorkbook.createFont();
                defaultFont.setFontHeightInPoints((short)10);
                defaultFont.setFontName("Arial");
                defaultFont.setColor(IndexedColors.BLACK.getIndex());
                defaultFont.setBold(false);
                defaultFont.setItalic(false);

                HSSFFont font= hssfWorkbook.createFont();
                font.setFontHeightInPoints((short)10);
                font.setFontName("Arial");
                font.setColor(IndexedColors.BLACK.getIndex());
                font.setBold(true);
                font.setItalic(false);

                CellStyle headerStyle = hssfSheet.getWorkbook().createCellStyle();
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setFont(font);

                CellStyle itemStyle1 = hssfSheet.getWorkbook().createCellStyle();
                itemStyle1.setAlignment(HorizontalAlignment.LEFT);
                itemStyle1.setFont(defaultFont);

                CellStyle itemStyle2 = hssfSheet.getWorkbook().createCellStyle();
                itemStyle2.setAlignment(HorizontalAlignment.RIGHT);
                itemStyle2.setFont(defaultFont);

                HSSFRow hssfRow_header = hssfSheet.createRow(0);
                HSSFCell hssfCell_header1 = hssfRow_header.createCell(0);
                hssfCell_header1.setCellValue("Tên");
                hssfCell_header1.setCellStyle(headerStyle);
                HSSFCell hssfCell_header2 = hssfRow_header.createCell(1);
                hssfCell_header2.setCellValue("Điểm số");
                hssfCell_header2.setCellStyle(headerStyle);

                int rowCount=0;
                for(int i = 0;i<adapter.getCount();i++)
                {
                    HSSFRow hssfRow = hssfSheet.createRow(++rowCount);

                    HSSFCell hssfCell0 = hssfRow.createCell(0);
                    hssfCell0.setCellStyle(itemStyle1);
                    hssfCell0.setCellValue(scoreClassList.get(i).getDescription());

                    HSSFCell hssfCell1 = hssfRow.createCell(1);
                    hssfCell1.setCellStyle(itemStyle2);
                    hssfCell1.setCellValue(scoreClassList.get(i).getPoint());

                }

                try {
                    FileOutputStream fileOutputStream= new FileOutputStream(filePath);
                    hssfWorkbook.write(fileOutputStream);



                    if (fileOutputStream!=null){
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }

                    Toast.makeText(getApplicationContext(),"Xuất file điểm thành công. Tên file: "+filename,Toast.LENGTH_SHORT).show();
                    Log.i("success","success");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }

    private void setupPieChart(){

        List<PieEntry> pieEntires = new ArrayList<>();
        for(int i = 0; i< classify_count.length; i++){
            pieEntires.add(new PieEntry(classify_count[i], classify[i]));
        }
        PieDataSet dataSet = new PieDataSet(pieEntires,"");

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        //Get the chart
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.setCenterText(assignName);
        pieChart.setCenterTextColor(R.color.black);
        pieChart.setDrawEntryLabels(false);
        pieChart.setContentDescription("");
        pieChart.setEntryLabelTextSize(16);
        pieChart.setHoleRadius(50);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(30,0,0,0);

        //legend attributes
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);  //Set the legend horizontal display
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); //top
        //legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); //Right to it
        legend.setXEntrySpace(7f);//x axis spacing
        legend.setYEntrySpace(10f); //Y axis spacing
        legend.setYOffset(10f);  //Y offset of the legend
        legend.setXOffset(10f);  //Offset of legend x
        legend.setTextSize(12);
        legend.setFormSize(20);
        legend.setFormToTextSpace(2);
    }

    private void setUpListView()
    {

    }
}