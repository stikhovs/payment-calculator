
package paymentcalculation;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

public class CellItem {
    
    private String value = "";
    private Workbook wb;
    private int rowNum;
    private int colNum;
    
    private void setValue(String s){
        this.value = s;
    }
    
    public String getValue(){
        return this.value.trim();
    }
    
    public int getRowNum(){
        return this.rowNum;
    }
    
    public int getColNum(){
        return this.colNum;
    }
    
    public String getCellCoords(){
        return String.valueOf(rowNum)+","+String.valueOf(colNum);
    } 
    
    public void lookAtValueType(Cell cell){
        CellType cellType = cell.getCellTypeEnum();
        switch(cellType){
            case BLANK: this.setValue("0"); break;
            case BOOLEAN: this.setValue(String.valueOf(cell.getBooleanCellValue())); break;
            case ERROR: this.setValue("Error"); break;
            case FORMULA: FormulaEvaluator evaluator = this.wb.getCreationHelper().createFormulaEvaluator();
                          CellType newCellType = evaluator.evaluateFormulaCellEnum(cell);
                          cell.setCellType(newCellType);
                          this.lookAtValueType(cell);
                          break;
            case NUMERIC: this.setValue(String.valueOf(cell.getNumericCellValue())); break;
            case STRING: this.setValue(cell.getStringCellValue()); break;
            case _NONE: this.setValue(""); break;
        }
    }        
    
    CellItem(Cell cell, Workbook wb){
        this.wb = wb;
        this.lookAtValueType(cell);
        this.getValue();
        this.rowNum = cell.getRowIndex();
        this.colNum = cell.getColumnIndex();
    }
}

