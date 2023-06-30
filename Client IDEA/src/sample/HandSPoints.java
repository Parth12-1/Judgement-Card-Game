package sample;

public class HandSPoints {
    private String Sir;
    private int SPointScored = 0;
    private int SPointWanted = 0;
    private int SPoints = 0;
    private int CPointsScored = 0;
    private int CPointsWanted = 0;
    private int CPoints = 0;

    public HandSPoints(String sir){
        this.Sir = sir;
        this.SPoints = 0;
        this.CPoints = 0;
        this.SPointScored = 0;
        this.SPointWanted = 0;
        this.CPointsScored = 0;
        this.CPointsWanted = 0;
    }

    public void setSPointScored(int sPointScored) {
        SPointScored = sPointScored;
        if (SPointScored == SPointWanted){
            SPoints = 10 + SPointScored;
        }
    }

    public void setSPointWanted(int SPointWanted) {
        this.SPointWanted = SPointWanted;
    }

    public void setCPointsScored(int cPointsScored) {
        CPointsScored = cPointsScored;
        if (CPointsScored == CPointsWanted){
            CPoints = 10 + CPointsScored;
        }
    }

    public void setCPointsWanted(int CPointsWanted) {
        this.CPointsWanted = CPointsWanted;
    }

    public void setCPoints(int CPoints) {
        this.CPoints = CPoints;
    }

    public void setSir(String sir) {
        Sir = sir;
    }

    public void setSPoints(int SPoints) {
        this.SPoints = SPoints;
    }

    public int getSPoints() {
        return SPoints;
    }

    public int getCPoints() {
        return CPoints;
    }

    public String getSir() {
        return Sir;
    }

    public int getCPointsScored() {
        return CPointsScored;
    }

    public int getCPointsWanted() {
        return CPointsWanted;
    }

    public int getSPointScored() {
        return SPointScored;
    }

    public int getSPointWanted() {
        return SPointWanted;
    }

}

