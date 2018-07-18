package io.egia.mqi;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureRepository;
import io.egia.mqi.version.Version;
import io.egia.mqi.version.VersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DatabaseManager {
    private Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private String versionsDbDirectory;
    private String[] dbObjects = {"func", "table", "view", "proc", "data", "meas"};
    private SqlExecutor sqlExec;
    private MeasureRepository measureRepository;

    public DatabaseManager(SqlExecutor sqlExec, MeasureRepository measureRepository, VersionRepository versionRepository) {
        this.sqlExec = sqlExec;
        this.measureRepository = measureRepository;
    }

    public void setVersionsDirectory(String versionsDirectory) {
        this.versionsDbDirectory = versionsDirectory;
    }

    public Version applyVersion(Version v) {
        log.info(String.format("Applying update: %s", v.getVersionId()));

        for (int i = 0; i < dbObjects.length; i++) {
            if (isMeasure(dbObjects[i])) {
                importMeasures(getDbObjectsDirectory(v, i));
            } else {
                applySqlScripts(getDbObjectsDirectory(v, i));
            }
        }

        return v;
    }

    public void createVersionTable() {
        log.info("Creating version table");
        sqlExec.execute("drop table if exists version; " +
                "create table version (version_id varchar(10)); " +
                "insert into version (version_id) values ('0.0.0');", "");
    }

    public void dropVersionTable() {
        log.info("Drop the version table if it exists");
        sqlExec.execute("drop table if exists version;", "");
    }

    private boolean isMeasure(String dbObject) {
        return dbObject.equals("meas");
    }

    private String getDbObjectsDirectory(Version v, int i) {
        return versionsDbDirectory + File.separator + v.getVersionId() + File.separator + dbObjects[i];
    }

    private void importMeasures(String measuresDirecotry) {
        File measuresDirectory = new File(measuresDirecotry);

        for (final File f : Objects.requireNonNull(measuresDirectory.listFiles())) {
            if (!f.isDirectory()) {
                Measure m = new Measure();
                m.setMeasureName(f.getName());
                String measureLogicString =  getMeasureFileAsString(measuresDirecotry, f);
                m.setMeasureJson(measureLogicString);
                measureRepository.saveAndFlush(m);
            }
        }
    }

    private String getMeasureFileAsString(String measuresDirecotry, File f) {
        return getFileContentAsString(measuresDirecotry + File.separator + f.getName());
    }

    private void applySqlScripts(String objectsDirecotry) {
        File sqlObjectsFile = new File(objectsDirecotry + File.separator + "file_list.txt");

        if (sqlObjectsFile.exists()) {
            List<String> tmpSqlFiles = new ArrayList<String>();

            try {
                Files.lines(Paths
                        .get(sqlObjectsFile.toString()), StandardCharsets.UTF_8)
                        .forEach(tmpSqlFiles::add);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for (String s : tmpSqlFiles) {
                File sqlFile = new File(objectsDirecotry + File.separator + s);
                if (sqlFile.exists()) {
                    sqlExec.execute(getFileContentAsString(sqlFile.toString()), sqlFile.toString());
                }
            }
        }
    }

    private String getFileContentAsString(String path) {
        StringBuilder sb = new StringBuilder();

        try {
            InputStream is = new FileInputStream(path);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String line = buf.readLine();

            while (line != null) {
                sb.append(line.trim()).append("\n");
                line = buf.readLine();
            }

            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }
}
