package com.eccolimp.cacamba_manager.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_settings")
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled = true;

    @Column(name = "from_email", length = 120)
    private String fromEmail;

    @Column(name = "from_name", length = 120)
    private String fromName;

    @Column(name = "report_to_email", length = 120)
    private String reportToEmail;

    // Horário diário de notificações de vencimento (24h, formato HH:mm)
    @Column(name = "vencimento_time", length = 5)
    private String vencimentoTime; // ex: "08:00"

    // Horário diário do relatório (24h, formato HH:mm)
    @Column(name = "relatorio_time", length = 5)
    private String relatorioTime; // ex: "09:00"

    // Dias da semana para rodar notificações/relatórios (Mon..Sun)
    @Column(name = "run_mon", nullable = false)
    private boolean runMon = true;

    @Column(name = "run_tue", nullable = false)
    private boolean runTue = true;

    @Column(name = "run_wed", nullable = false)
    private boolean runWed = true;

    @Column(name = "run_thu", nullable = false)
    private boolean runThu = true;

    @Column(name = "run_fri", nullable = false)
    private boolean runFri = true;

    @Column(name = "run_sat", nullable = false)
    private boolean runSat = true;

    @Column(name = "run_sun", nullable = false)
    private boolean runSun = true;

    // Timezone IANA (ex: America/Sao_Paulo)
    @Column(name = "time_zone", length = 50)
    private String timeZone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getReportToEmail() {
        return reportToEmail;
    }

    public void setReportToEmail(String reportToEmail) {
        this.reportToEmail = reportToEmail;
    }

    public String getVencimentoTime() {
        return vencimentoTime;
    }

    public void setVencimentoTime(String vencimentoTime) {
        this.vencimentoTime = vencimentoTime;
    }

    public String getRelatorioTime() {
        return relatorioTime;
    }

    public void setRelatorioTime(String relatorioTime) {
        this.relatorioTime = relatorioTime;
    }

    public boolean isRunMon() {
        return runMon;
    }

    public void setRunMon(boolean runMon) {
        this.runMon = runMon;
    }

    public boolean isRunTue() {
        return runTue;
    }

    public void setRunTue(boolean runTue) {
        this.runTue = runTue;
    }

    public boolean isRunWed() {
        return runWed;
    }

    public void setRunWed(boolean runWed) {
        this.runWed = runWed;
    }

    public boolean isRunThu() {
        return runThu;
    }

    public void setRunThu(boolean runThu) {
        this.runThu = runThu;
    }

    public boolean isRunFri() {
        return runFri;
    }

    public void setRunFri(boolean runFri) {
        this.runFri = runFri;
    }

    public boolean isRunSat() {
        return runSat;
    }

    public void setRunSat(boolean runSat) {
        this.runSat = runSat;
    }

    public boolean isRunSun() {
        return runSun;
    }

    public void setRunSun(boolean runSun) {
        this.runSun = runSun;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}


