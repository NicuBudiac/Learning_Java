package com;

import com.gilat.automation.common.enums.FolderType;
import com.gilat.automation.common.enums.database.DbName;
import com.gilat.automation.common.utils.IPCalcSchema;
import com.gilat.automation.common.utils.config.SetupConfig;
import com.gilat.automation.common.utils.config.database.PostgresConfig;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class SiteDataSource {

    /**
     * This object is used for connections to the physical data source (our Database). It takes as parameters url, username and password of database
     */
    private static DataSource dataGWSource;
    private static DataSource dataDCSource;
    private static DataSource dataSourceType;
    public String siteType;

    public DataSource createGWDataSource() {
        int firstSite = SetupConfig.Sites.GWM.getSites()[0];
        PostgresConfig pgconf = SetupConfig.Sites.GWM.getConfigs(firstSite).getPostgresConfig();
        dataDCSource = new DriverManagerDataSource(
                String.format("jdbc:postgresql://%s:%s/%s",
                        pgconf.getHost(),
                        pgconf.getPort(),
                        DbName.GWM.getName()),
                pgconf.getPgUser(),
                pgconf.getPgPassword()
        );
        return dataDCSource;
    }

    public DataSource createDCDataSource() {
        int firstSite = SetupConfig.Sites.DCM.getSites()[0];
        PostgresConfig pgconf = SetupConfig.Sites.DCM.getConfigs(firstSite).getPostgresConfig();
        dataDCSource = new DriverManagerDataSource(
                String.format("jdbc:postgresql://%s:%s/%s",
                        pgconf.getHost(),
                        pgconf.getPort(),
                        DbName.DCM.getName()),
                pgconf.getPgUser(),
                pgconf.getPgPassword()
        );
        return dataDCSource;
    }

//    public DataSource creeateDCDataSource() {
//        PostgresConfig postgresConn = PostgresConfig().
//                withPgIp(IPCalcSchema.getSitePostgresIP(FolderType.SiteDC, 2)).
//        SetupConfig.Sites.DCM.getConfigs(2).getPostgresConfig().getHost()
//        withPgPort(5432).
//                withPgUser("postgres").
//                withSiteId(2)
//                .withPgDatabaseName(DbName.DCM);
//        int firstSite = SetupConfig.Sites.DCM.getSites()[0];
//        PostgresConfig pgconf = SetupConfig.Sites.DCM.getConfigs(firstSite).getPostgresConfig();
//        dataDCSource = new DriverManagerDataSource(
//                String.format("jdbc:postgresql://%s:%s/%s",
//                        pgconf.getHost(),
//                        pgconf.getPort(),
//                        DbName.DCM.getName()),
//                pgconf.getPgUser(),
//                pgconf.getPgPassword()
//        );
//        return dataDCSource;
//    }

    public DataSource getSiteType(String siteType){
        this.siteType = siteType;
        if (siteType.contains("M")){
            dataSourceType= createGWDataSource();
        }
        else if(siteType.contains("S")){
            dataSourceType= createDCDataSource();
        }
        return dataSourceType;
    }
}
