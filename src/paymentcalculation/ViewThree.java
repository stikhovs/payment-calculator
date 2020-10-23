package paymentcalculation;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.DoubleAdder;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

class Condition {

    private boolean isProgressTime = true;

    public boolean isProgressTime() {
        return isProgressTime;
    }

    public void setProgressTime(boolean isProgressTime) {
        this.isProgressTime = isProgressTime;
    }
}

public class ViewThree implements Initializable {

    String[] groupInfoCells = {"A1", "A3", "E3", "R1", "R3", "Y1", "Y3", "AF2"};
    String[] studentNameCells = {"B14", "B15", "B16", "B17", "B18", "B19", "B20", "B21", "B22", "B23", "B24", "B25", "B26", "B27", "B28", "B29", "B30", "B31", "B32", "B33"};
    String[] studentBalanceCells = {"AF14", "AF15", "AF16", "AF17", "AF18", "AF19", "AF20", "AF21", "AF22", "AF23", "AF24", "AF25", "AF26", "AF27", "AF28", "AF29", "AF30", "AF31", "AF32", "AF33"};
    /*String[] weekDaysOne = {"AO2", "AP2", "AQ2", "AR2", "AS2", "AT2", "AU2"};
    String[] weekDaysTwo = {"AO4", "AP4", "AQ4", "AR4", "AS4", "AT4", "AU4"};*/
    String[] weekDaysOne = {"AY2", "AZ2", "BA2", "BB2", "BC2", "BD2", "BE2"};
    String[] weekDaysTwo = {"AY4", "AZ4", "BA4", "BB4", "BC4", "BD4", "BE4"};
    String[] indGraphicCells = {"H14", "H15", "H16", "H17", "H18", "H19", "H20", "H21", "H22", "H23", "H24", "H25", "H26", "H27", "H28", "H29", "H30", "H31", "H32", "H33"};
    String singleDiscountColumn = "M14";
    String permanentDiscountColumn = "O14";

    String fileName = "Расчет квитанций - " + ViewOne.chosenMonth + " - " + ViewOne.chosenYear + ".xlsx";

    private int rowStartMon = 0;
    private int rowStartTue = 0;
    private int rowStartSat = 0;
    private int rowStartInd = 0;
    private int rowStartOther = 0;

    ExecutorService pool;
//Executors.newWorkStealingPool();
    //ExecutorService poolForSheetReading = Executors.newFixedThreadPool(4);
    //Executors.newCachedThreadPool();

//    List<String> nextMonthHolidays = new ArrayList<>();
//    List<String> nextMonthDaysOfWeek = new ArrayList<>();
//    List<String> nextMonthDays = new ArrayList<>();
    List<GroupInfo> groups = new ArrayList<>();

    @FXML
    Button calculateButton;
    @FXML
    Button backButton;
    @FXML
    Button openReportButton;
    @FXML
    Button chooseSavePathButton;
    @FXML
    TextField saveFileTextField;

    @FXML
    Label finished;
    @FXML
    Label progressLabel;
    @FXML
    ProgressBar progressBar;
    @FXML
    ProgressIndicator progressIndicator;
    volatile DoubleAdder progress = new DoubleAdder();
    volatile double percent = 0;

    Condition condition = new Condition();

    @FXML
    private void saveFileAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));

        File file = dc.showDialog(null);

        if (file != null) {
            ViewOne.savedPath = file.getAbsolutePath();
            saveFileTextField.setText(ViewOne.savedPath);
            calculateButton.setDisable(false);
        } else if (ViewOne.savedPath.isEmpty()) {
            System.out.println("Путь сохранения остался прежним");
            //saveFileTextField.setText("Укажите путь сохранения файла");
            calculateButton.setDisable(true);
        }
    }

    @FXML
    private void SceneTwoAction(ActionEvent event) throws IOException {
        Parent next = FXMLLoader.load(getClass().getResource("ViewTwo.fxml"));
        Scene sceneTwo = new Scene(next);
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage) source.getScene().getWindow();
        thisStage.setScene(sceneTwo);
        thisStage.show();

//          FXMLLoader loader = new FXMLLoader();
//          loader.setLocation(getClass().getResource("ViewTwo.fxml"));
//          Parent next = loader.load();
//          Scene sceneTwo = new Scene(next);
//          Calculation calc = loader.getController();
//          chosenFile = calc.chosenFile;
//          System.out.println("JOPA: "+chosenFile);
//          Node source = (Node) event.getSource();
//          Stage thisStage = (Stage)source.getScene().getWindow();
//          thisStage.setScene(sceneTwo);
//          thisStage.show();
    }

    private static boolean isNameListEmpty(XSSFSheet sheet) {
        List<String> nameList = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() > 36) {
                break;
            }
            // Get iterator to all cells of current row
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (cell.getColumnIndex() > 36) {
                    break;
                }

                if (cell.getColumnIndex() == 1 && cell.getRowIndex() >= 13 && cell.getRowIndex() < 32) {

                    CellType cellType = cell.getCellTypeEnum();
                    switch (cellType) {
                        case NUMERIC:
                            nameList.add(String.valueOf(cell.getNumericCellValue()));
                            break;
                        case STRING:
                            nameList.add(cell.getStringCellValue());
                            break;
                        default:
                            continue;
                    }
                }
            }
        }
        if (nameList.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private static ArrayList<String> setDataCells(String[] coords) {
        ArrayList<String> data = new ArrayList<>();
        for (String s : coords) {
            data.add(s);
        }
        return data;
    }

    private void createNextMonthDays() {

        GregorianCalendar c = new GregorianCalendar(ViewOne.chosenYear, ViewOne.chosenMonthNumber, 1);
        System.out.println("chosen year: " + ViewOne.chosenYear);
        System.out.println("chosen month: " + ViewOne.chosenMonthNumber);
        System.out.println("calendar: " + c.getTime());
        int max = c.getActualMaximum(c.DAY_OF_MONTH);

        for (int i = 1; i <= max; i++) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("EE");
            //System.out.println(i + ": " + c.get(c.DAY_OF_WEEK) + " - " + dateFormat.format(c.getTime()));
            ViewTwo.nextMonthDaysOfWeek.add(dateFormat.format(c.getTime()));
            SimpleDateFormat holidayFormat = new SimpleDateFormat("yyyy-MM-dd");
            ViewTwo.nextMonthDays.add(holidayFormat.format(c.getTime()));
            c.add(c.DATE, 1);
        }
    }

    public double discountPayment(Student student, GroupInfo group) {
        double discountPay = 0.0;
        if (student.getDiscount() != 0) {
            discountPay = (group.getPricePerHour() - (group.getPricePerHour() / 100 * student.getDiscount())) * student.countPayment2N();
        } else {
            discountPay = group.getPricePerHour() * student.countPayment2N();
        }
        return discountPay;
    }

    private XSSFWorkbook createReport() {
        XSSFWorkbook report = new XSSFWorkbook();

        report.createSheet("пн ср птн");
        report.createSheet("вт чт");
        report.createSheet("сб");
        report.createSheet("инд");
        report.createSheet("другое");

//        try (FileOutputStream out = new FileOutputStream(new File(saveFileTextField.getText()) + File.separator + fileName)) {
//                report.write(out);
//            } catch (IOException e) {
//                System.out.println("Сначала закройте документ");
//                e.printStackTrace();
//            }
        return report;
    }

    public String checkForGroupSheetName(GroupInfo group) {

        String sheetName = "";

        if (!group.getIsIndividual()) {
            if (group.getIsMonWenFr()) {
                sheetName = "пн ср птн";
            } else if (group.getIsTueTh()) {
                sheetName = "вт чт";
            } else if (group.getIsSat()) {
                sheetName = "сб";
            } else if (group.getIsOtherSchedule()) {
                sheetName = "другое";
            }
        } else {
            sheetName = "инд";
        }
        return sheetName;
    }

    public void createNEWExcelTable(XSSFWorkbook report, String sheetName, GroupInfo group) throws FileNotFoundException, IOException {

        XSSFSheet sheet = report.getSheet(sheetName);
        System.out.println("sheetName: " + sheet.getSheetName());

        int rowCount = 0;

        switch (sheetName) {
            case "пн ср птн":
                rowCount = rowStartMon;
                break;
            case "вт чт":
                rowCount = rowStartTue;
                break;
            case "сб":
                rowCount = rowStartSat;
                break;
            case "другое":
                rowCount = rowStartOther;
                break;
            case "инд":
                rowCount = rowStartInd;
                break;
        }

        int cellCount = 0;
        final int columnQuantity = 5;

        CellStyle groupInfo = report.createCellStyle();
        groupInfo.setWrapText(true);
        groupInfo.setAlignment(HorizontalAlignment.CENTER);
        groupInfo.setBorderTop(BorderStyle.THIN);
        groupInfo.setBorderBottom(BorderStyle.THIN);
        groupInfo.setBorderLeft(BorderStyle.THIN);

        CellStyle payHour = report.createCellStyle();
        payHour.setDataFormat((short) 2);
        payHour.setAlignment(HorizontalAlignment.CENTER);

        CellStyle center = report.createCellStyle();
        center.setAlignment(HorizontalAlignment.CENTER);

        CellStyle subHeader = report.createCellStyle();
        subHeader.setAlignment(HorizontalAlignment.CENTER);
        subHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        subHeader.setBorderRight(BorderStyle.THIN);
        subHeader.setBorderBottom(BorderStyle.THIN);
        subHeader.setBorderLeft(BorderStyle.THIN);
        subHeader.setAlignment(HorizontalAlignment.CENTER);
        subHeader.setWrapText(true);
        Font subHeaderFont = report.createFont();
        subHeaderFont.setBold(true);
        subHeader.setFont(subHeaderFont);

        CellStyle leftBorder = report.createCellStyle();
        leftBorder.setBorderLeft(BorderStyle.THIN);
        CellStyle rightBorder = report.createCellStyle();
        rightBorder.setBorderRight(BorderStyle.THIN);

        CellStyle rublePay = report.createCellStyle();
        rublePay.setAlignment(HorizontalAlignment.CENTER);
        rublePay.setBorderRight(BorderStyle.THIN);

        CellStyle sheetHeader = report.createCellStyle();
        sheetHeader.setAlignment(HorizontalAlignment.CENTER);
        Font sheetHeaderFont = report.createFont();
        sheetHeaderFont.setBold(true);
        sheetHeaderFont.setFontHeightInPoints((short) 16);
        sheetHeader.setFont(sheetHeaderFont);

        Row row = sheet.createRow(rowCount);
        cellCount = 0;
        Cell cell = row.createCell(cellCount);
        if (rowCount == 0) {
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1));
            cell.setCellValue(sheet.getSheetName() + " " + ViewOne.chosenMonth);
            cell.setCellStyle(sheetHeader);
            row = sheet.createRow(++rowCount);
        }

        /* Создание шапки с информацией по группе */
        cellCount = 0;
        sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1));
        RegionUtil.setBorderBottom(BorderStyle.THIN, new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1), sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1), sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1), sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1), sheet);
        cell = row.createCell(cellCount);
        cell.setCellValue(group.getGroupId() + " (" + group.getSheetName() + "), " + (int) group.getPricePerHour() + " р/ач " + ", Начало: " + group.getClassStartTime() + ", " + group.sheduleDaysToString()
                + "\n" + group.getTeachers() + ", " + group.getGroupLevel() + ", Длительность: " + group.getDuration() + " а/ч" + ", "
                + "\n" + "Часов в следующем месяце: " + String.format("%.2f", group.getNextMonthHours()));
        cell.getRow().setHeight((short) (cell.getRow().getHeight() * 3));

        //System.out.println(group.getSheetName() + ":" + group.getPricePerHour());
        cell.setCellStyle(groupInfo);

        cell = row.createCell(columnQuantity);
        cell.setCellStyle(leftBorder);

        row = sheet.createRow(++rowCount);

        /* Создание подшапки (названия колонок) */
        cellCount = 0;
        cell = row.createCell(cellCount);
        cell.setCellValue("№");
        cell.setCellStyle(subHeader);
        cell = row.createCell(++cellCount);
        cell.setCellValue("ФИО студента");
        cell.setCellStyle(subHeader);
        cell = row.createCell(++cellCount);
        cell.setCellValue("Скидка");
        cell.setCellStyle(subHeader);
        cell = row.createCell(++cellCount);
        cell.setCellValue("Часов \nк оплате");
        cell.setCellStyle(subHeader);
        cell = row.createCell(++cellCount);
        cell.setCellValue("Оплата \n в руб");
        cell.setCellStyle(subHeader);

        /* Заполнение таблицы информацией о студентах */
        row = sheet.createRow(++rowCount);

        int studentNumber = 1;
        for (Student student : group.getStudentsInfo()) {
            cellCount = 0;
            cell = row.createCell(cellCount);
            cell.setCellValue(studentNumber + ")");
            studentNumber++;
            cell.setCellStyle(leftBorder);

            cell = row.createCell(++cellCount);
            cell.setCellValue(student.getName());

            cell = row.createCell(++cellCount);
            if (student.getDiscount() != 0) {
                cell.setCellValue((int) student.getDiscount() + "%");
            }
            cell.setCellStyle(center);

            cell = row.createCell(++cellCount);
            if (student.countPayment() <= 0.004) {
                cell.setCellValue("не должен");
            } else {
                cell.setCellValue(student.countPayment());
            }
            cell.setCellStyle(payHour);

            cell = row.createCell(++cellCount);
            if (student.countPayment() <= 0.004) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(String.format("%.2f", discountPayment(student, group)) + " руб");
            }
            cell.setCellStyle(rublePay);

            cell = row.createCell(++cellCount);
            cell.setCellStyle(leftBorder);

            RegionUtil.setBorderBottom(BorderStyle.DASHED, new CellRangeAddress(rowCount, rowCount, 0, cellCount - 1), sheet);

            row = sheet.createRow(++rowCount);
        }

        cellCount = 0;
        sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1));
        RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1), sheet);
        row.setHeight((short) (cell.getRow().getHeight() * 0.5));
        row = sheet.createRow(++rowCount);

        switch (sheetName) {
            case "пн ср птн":
                rowStartMon = row.getRowNum();
                break;
            case "вт чт":
                rowStartTue = row.getRowNum();
                break;
            case "сб":
                rowStartSat = row.getRowNum();
                break;
            case "другое":
                rowStartOther = row.getRowNum();
                break;
            case "инд":
                rowStartInd = row.getRowNum();
                break;
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
        //inputStream.close();
        // Write File
//       FileOutputStream out = new FileOutputStream(new File(saveFileTextField.getText()) + File.separator + fileName);
//       report.write(out);
//       out.close();

    }

    @FXML
    private void calculateAction(ActionEvent event) {
        pool = Executors.newWorkStealingPool();
        calculateButton.setDisable(true);
        backButton.setDisable(true);
        chooseSavePathButton.setDisable(true);
        progressLabel.setVisible(true);

        XSSFWorkbook report = createReport();
        //createReport();

        pool.submit(() -> {

            synchronized (condition) {
                progressBar.setVisible(true);
                progressIndicator.setVisible(true);
                double start = 0.0;
                while (condition.isProgressTime()) {
                    System.out.println("PROGRESS: " + progress.sum());
                    for (double i = start; i <= progress.sum(); i += 0.01) {
                        progressBar.setProgress(i);
                        progressIndicator.setProgress(i);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            System.out.println("Поток прогресса прерван");
                        }
                    }
                    condition.setProgressTime(false);
                    condition.notify();
                    try {
                        condition.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    start = progress.sum();
//                    if(progressIndicator.getProgress() == 1.0){
//                        pool.shutdown();
//                    }                

                }
            }

        });

        //progress.add(0.2);
        pool.submit(() -> {
            //try (FileInputStream fileInputStream = new FileInputStream(new File(ViewOne.chosenFile))) {
            try {

                File file = new File(ViewOne.chosenFile);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText("Запуск расчета...");
                    }
                });

                System.out.println("РАБОТАЮ С ФАЙЛОМ: " + ViewOne.chosenFile);
                //XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(file));
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                int sheetsNumber = workbook.getNumberOfSheets();
                /* Создание коллекции, содержащей дни недели выбранного месяца */
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText("Расчет дней недели выбранного месяца");
                    }
                });
                createNextMonthDays();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText("Сбор информации из файла " + ViewOne.fileName);
                    }
                });

                List<XSSFSheet> sheets = new ArrayList<>();

                for (int i = 0; i < sheetsNumber; i++) {
                    XSSFSheet sheet = workbook.getSheetAt(i);

                    if (workbook.isSheetHidden(i)) {
                        continue; // если лист скрыт, то пропускаем
                    }
                    if (sheet.getSheetName().equals("cv")) {
                        continue;
                    }
                    if (sheet.getSheetName().equals("OSTATKY")) {
                        continue;
                    }
                    if (sheet.getSheetName().equalsIgnoreCase("ostatki")) {
                        continue;
                    }
                    if (sheet.getSheetName().equals("OS")) {
                        continue;
                    }
                    if (sheet.getSheetName().equals("оста")) {
                        continue;
                    }
                    if (sheet.getSheetName().equals("ф_01(03)")) {
                        continue;
                    }
                    if (sheet.getSheetName().equals("ф_02(03)")) {
                        continue;
                    }
                    if (sheet.getSheetName().equals("ф_03(03)")) {
                        continue;
                    }

                    /* Отбор нужных листов (критерий - наличие в списке фамилий хотя бы одной записи) */
                    if (!isNameListEmpty(sheet)) {
                        sheets.add(sheet);
                    }
                    /* Конец отбора, коллекция нужных листов сформирована */
                }
                percent = (100D / (double) sheets.size()) / 100D;
                System.out.println("Кол-во листов: " + sheets.size());
                System.out.println("Один лист это: " + percent + "%");

                /* Создание и заполнение данными объекта класса GroupInfo */
                //progress.add(0.2);
                /* Формирование общих данных о группе: стоимость часа, номер группы, продолжительность занятия, преподаватели и т.д. */
                ArrayList<String> dataCells = setDataCells(groupInfoCells);

                for (XSSFSheet sheetItem : sheets) {
                    synchronized (condition) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                progressLabel.setText("Обработка группы: " + sheetItem.getSheetName());
                            }
                        });

                        GroupInfo groupInfo = new GroupInfo();
                        groupInfo.setSheetName(sheetItem.getSheetName());
                        
                        for (String dataCell : dataCells) {

                            CellAddress address = new CellAddress(dataCell);
                            sheetItem.setActiveCell(address);

                            XSSFRow rowItem = sheetItem.getRow(sheetItem.getActiveCell().getRow());
                            int col = sheetItem.getActiveCell().getColumn();
                            XSSFCell cellOne = rowItem.getCell(col);

                            CellItem cellItem = new CellItem(cellOne, workbook);
                            cellItem.lookAtValueType(cellOne);

                            String coords = cellOne.getAddress().toString();
                            switch (coords) {
                                case ("A1"):
                                	try {
                                		groupInfo.setPricePerHour(Double.parseDouble(cellItem.getValue()));
                                		break;
                                	}
	                                catch(NumberFormatException e) {
	                                	System.out.println("В ячейке " + coords + " листа " + groupInfo.getSheetName() + " ожидалось число");
	                                	groupInfo.setPricePerHour(0);
	                                }
                                case ("A3"):
                                    groupInfo.setGroupId(cellItem.getValue());
                                    break;
                                case ("E3"):
                                    groupInfo.setGroupLevel(cellItem.getValue());
                                    break;
                                case ("R1"):
                                    groupInfo.setTeacherOne(cellItem.getValue());
                                    break;
                                case ("R3"):
                                    groupInfo.setTeacherTwo(cellItem.getValue());
                                    break;
                                case ("Y1"):
                                	try {
                                        groupInfo.setClassDurationOne(Double.parseDouble(cellItem.getValue()));
                                      System.out.println("Cell value: " + cellItem.getValue());
                                      System.out.println("After parsing: " + Double.parseDouble(cellItem.getValue()));
                                      System.out.println("Group next month hours: " + groupInfo.getNextMonthHours());
                                        break;
                                	}
                                	catch(NumberFormatException e) {
	                                	System.out.println("В ячейке " + coords + " листа " + groupInfo.getSheetName() + " ожидалось число");
	                                	groupInfo.setClassDurationOne(0);
	                                }
                                case ("Y3"):
                                	try {
                                		groupInfo.setClassDurationTwo(Double.parseDouble(cellItem.getValue()));
                                        break;
                                	}
                                	catch(NumberFormatException e) {
	                                	System.out.println("В ячейке " + coords + " листа " + groupInfo.getSheetName() + " ожидалось число");
	                                	groupInfo.setClassDurationTwo(0);
	                                }
                                case ("AF2"):
                                    groupInfo.setClassStartTime(String.format("%tH:%tM", cellOne.getDateCellValue(), cellOne.getDateCellValue()));
                                    break;
                            }

                        }

                        /* Формирование списка студентов группы и остатка академ. часов каждого */
                        for (String studentNameCell : studentNameCells) {

                            CellAddress address = new CellAddress(studentNameCell);
                            sheetItem.setActiveCell(address);

                            XSSFRow rowItem = sheetItem.getRow(sheetItem.getActiveCell().getRow());
                            int col = sheetItem.getActiveCell().getColumn();
                            XSSFCell cellOne = rowItem.getCell(col);
                            CellItem cellItem = new CellItem(cellOne, workbook);
                            cellItem.lookAtValueType(cellOne);

                            if (cellItem.getValue().equals("0")) {
                                continue;
                            }

                            Student student = new Student();
                            student.setName(cellItem.getValue());

                            address = new CellAddress(studentBalanceCells[0]);
                            sheetItem.setActiveCell(address);
                            col = sheetItem.getActiveCell().getColumn();
                            cellOne = rowItem.getCell(col);
                            cellItem = new CellItem(cellOne, workbook);
                            cellItem.lookAtValueType(cellOne);

                            if (!cellItem.getValue().equals("Error")) {
                                student.setBalance(Double.parseDouble(cellItem.getValue()));
                            } else {
                                student.setBalance(0.0);
                            }

                            address = new CellAddress(indGraphicCells[0]);
                            sheetItem.setActiveCell(address);
                            col = sheetItem.getActiveCell().getColumn();
                            cellOne = rowItem.getCell(col);
                            cellItem = new CellItem(cellOne, workbook);
                            cellItem.lookAtValueType(cellOne);
                            if (cellItem.getValue() == "0") {
                                student.setIndGrahic(false);
                            } else {
                                student.setIndGrahic(true);
                            }

                            address = new CellAddress(singleDiscountColumn);
                            sheetItem.setActiveCell(address);
                            col = sheetItem.getActiveCell().getColumn();
                            cellOne = rowItem.getCell(col);
                            cellItem = new CellItem(cellOne, workbook);
                            cellItem.lookAtValueType(cellOne);
                            if (cellItem.getValue() == "0") {
                                address = new CellAddress(permanentDiscountColumn);
                                sheetItem.setActiveCell(address);
                                col = sheetItem.getActiveCell().getColumn();
                                cellOne = rowItem.getCell(col);
                                cellItem = new CellItem(cellOne, workbook);
                                cellItem.lookAtValueType(cellOne);
                                if (cellItem.getValue() != "0") {
                                    student.setDiscount(Double.parseDouble(cellItem.getValue().replaceAll("%", "")));
                                }
                            } else {
                                student.setDiscount(Double.parseDouble(cellItem.getValue().replaceAll("%", "")));
                            }

                            groupInfo.addStudent(student);

                        }

                        //progress.add(0.2);
                        /* Формирование расписания группы по дням недели */
                        int dayNumber = 1;
                        for (String day : weekDaysOne) {
                            CellAddress address = new CellAddress(day);
                            sheetItem.setActiveCell(address);

                            XSSFRow rowItem = sheetItem.getRow(sheetItem.getActiveCell().getRow());
                            int col = sheetItem.getActiveCell().getColumn();
                            XSSFCell cellOne = rowItem.getCell(col);
                            CellItem cellItem = new CellItem(cellOne, workbook);
                            cellItem.lookAtValueType(cellOne);
                            if (cellItem.getValue().equals("true")) {
                                switch (dayNumber) {
                                    case 1:
                                        groupInfo.addClassDayOne("пн");
                                        break;
                                    case 2:
                                        groupInfo.addClassDayOne("вт");
                                        break;
                                    case 3:
                                        groupInfo.addClassDayOne("ср");
                                        break;
                                    case 4:
                                        groupInfo.addClassDayOne("чт");
                                        break;
                                    case 5:
                                        groupInfo.addClassDayOne("пт");
                                        break;
                                    case 6:
                                        groupInfo.addClassDayOne("сб");
                                        break;
                                    case 7:
                                        groupInfo.addClassDayOne("вс");
                                        break;
                                }
                            }

                            address = new CellAddress(weekDaysTwo[0]);
                            sheetItem.setActiveCell(address);
                            rowItem = sheetItem.getRow(sheetItem.getActiveCell().getRow());
                            cellOne = rowItem.getCell(col);
                            cellItem = new CellItem(cellOne, workbook);
                            cellItem.lookAtValueType(cellOne);

                            if (cellItem.getValue().equals("true")) {
                                switch (dayNumber) {
                                    case 1:
                                        groupInfo.addClassDayTwo("пн");
                                        break;
                                    case 2:
                                        groupInfo.addClassDayTwo("вт");
                                        break;
                                    case 3:
                                        groupInfo.addClassDayTwo("ср");
                                        break;
                                    case 4:
                                        groupInfo.addClassDayTwo("чт");
                                        break;
                                    case 5:
                                        groupInfo.addClassDayTwo("пт");
                                        break;
                                    case 6:
                                        groupInfo.addClassDayTwo("сб");
                                        break;
                                    case 7:
                                        groupInfo.addClassDayTwo("вс");
                                        break;
                                }
                            }

                            dayNumber++;
                        }

                        int dateNumber = 0;
                        for (String weekDay : ViewTwo.nextMonthDaysOfWeek) {

                            System.out.println("WEEKDAY: " + weekDay);

                            if (!ViewTwo.nextMonthHolidays.contains(ViewTwo.nextMonthDays.get(dateNumber))) {

                                groupInfo.getClassDaysOne().forEach((classDay) -> {
                                    System.out.println("CLASSDAY: " + classDay);
                                    if (classDay.equalsIgnoreCase(weekDay)) {
                                        for (Student student : groupInfo.getStudentsInfo()) {
                                            student.setNextMonthHours(groupInfo.getClassDurationOne());
                                        }
                                        groupInfo.addNextMonthHours(groupInfo.getClassDurationOne());
                                    }
                                });
                                groupInfo.getClassDaysTwo().forEach((classDay) -> {
                                    if (classDay.equalsIgnoreCase(weekDay)) {
                                        for (Student student : groupInfo.getStudentsInfo()) {
                                            student.setNextMonthHours(groupInfo.getClassDurationTwo());
                                        }
                                        groupInfo.addNextMonthHours(groupInfo.getClassDurationTwo());
                                    }
                                });
                            }
                            dateNumber++;
                        }
                        
                        if (!ViewTwo.changedDaysMinus.isEmpty()) {
                                for (String changedWeekDay : ViewTwo.changedDaysMinus) {
                                    groupInfo.getClassDaysOne().forEach((classDay) -> {
                                        if (classDay.equalsIgnoreCase(changedWeekDay)) {
                                            for (Student student : groupInfo.getStudentsInfo()) {
                                                student.removeNextMonthHours(groupInfo.getClassDurationOne());
                                            }
                                            groupInfo.removeNextMonthHours(groupInfo.getClassDurationOne());
                                        }
                                    });
                                    groupInfo.getClassDaysTwo().forEach((classDay) -> {
                                        if (classDay.equalsIgnoreCase(changedWeekDay)) {
                                            for (Student student : groupInfo.getStudentsInfo()) {
                                                student.removeNextMonthHours(groupInfo.getClassDurationTwo());
                                            }
                                            groupInfo.removeNextMonthHours(groupInfo.getClassDurationTwo());
                                        }
                                    });
                                }
                            }
                            if (!ViewTwo.changedDaysPlus.isEmpty()) {
                                for (String changedWeekDay : ViewTwo.changedDaysPlus) {
                                    groupInfo.getClassDaysOne().forEach((classDay) -> {
                                        if (classDay.equalsIgnoreCase(changedWeekDay)) {
                                            for (Student student : groupInfo.getStudentsInfo()) {
                                                student.setNextMonthHours(groupInfo.getClassDurationOne());
                                            }
                                            groupInfo.addNextMonthHours(groupInfo.getClassDurationOne());
                                        }
                                    });
                                    groupInfo.getClassDaysTwo().forEach((classDay) -> {
                                        if (classDay.equalsIgnoreCase(changedWeekDay)) {
                                            for (Student student : groupInfo.getStudentsInfo()) {
                                                student.setNextMonthHours(groupInfo.getClassDurationTwo());
                                            }
                                            groupInfo.addNextMonthHours(groupInfo.getClassDurationTwo());
                                        }
                                    });
                                }
                            }

                        /* Объект класса GroupInfo сформирован */
 /* Создание коллекции групп */
                        //groups.add(groupInfo);
                        groupInfo.createSheduleDays();

                        createNEWExcelTable(report, checkForGroupSheetName(groupInfo), groupInfo);
                        progress.add(percent);

                        groupInfo.getClassDaysOne().clear();
                        groupInfo.getClassDaysTwo().clear();
                        groupInfo.getScheduleDays().clear();
                        for (Student student : groupInfo.getStudentsInfo()) {
                            student = null;
                        }
                        groupInfo.getStudentsInfo().clear();
                        condition.setProgressTime(true);
                        condition.notify();
                        condition.wait();
                    }

                    //System.out.println(groupInfo);
                    //System.out.println();
                }
                //progress.add(0.2);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText("Обработка завершена");
                    }
                });

                System.out.println("Обработка завершена");
//                progress = 0.8;  
                //writeReportAction();
                ViewTwo.nextMonthHolidays.clear();
                ViewTwo.nextMonthDaysOfWeek.clear();
                ViewTwo.nextMonthDays.clear();
                progressBar.setProgress(1.0);
                progressIndicator.setProgress(1.0);
                //progress.add(0.2);

                openReportButton.setVisible(true);
                try (FileOutputStream out = new FileOutputStream(new File(saveFileTextField.getText()) + File.separator + fileName)) {
                    report.write(out);
                } catch (IOException e) {
                    System.out.println("Сначала закройте документ");
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText("Excel файл успешно создан!");
                    }
                });

                System.out.println("Excel файл успешно создан!");
                System.out.println(saveFileTextField.getText());
                finished.setVisible(true);
                //progress.add(0.2);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
            //System.out.println("Поток обработки данных жив? " + Thread.currentThread().isAlive());
        });

    }

    @FXML
    private void openReportAction(ActionEvent event) {
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(new File(saveFileTextField.getText() + File.separator + fileName));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        saveFileTextField.setText(System.getProperty("user.home") + File.separator + "Desktop");
        progressBar.setVisible(false);
        progressIndicator.setVisible(false);
        finished.setVisible(false);
        openReportButton.setVisible(false);
        progressIndicator.setMinHeight(68.0);
        progressIndicator.setMinWidth(101.0);
        progressLabel.setVisible(false);
        progressLabel.setText("");
    }
}
