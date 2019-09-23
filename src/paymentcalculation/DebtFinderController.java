package paymentcalculation;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.DoubleAdder;
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

public class DebtFinderController implements Initializable {

    private String saveFilePath = "";
    private String chosenFile = "";
    private String fileName = "";
    private String savedPath = "";
    private ExecutorService pool;

    @FXML
    private Button readyButton;
    @FXML
    private Button backButton;
    @FXML
    private Button chooseFileButton;
    @FXML
    private Button saveFileButton;
    @FXML
    private Button openFileButton;
    @FXML
    private TextField filePathTextField;
    @FXML
    private TextField saveFilePathTextField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    Label finished;
    @FXML
    Label progressLabel;
    volatile DoubleAdder progress = new DoubleAdder();
    volatile double percent = 0;

    Condition condition = new Condition();

    String[] groupInfoCells = {"A1", "A3", "Y1", "Y3"};
    String[] studentNameCells = {"B14", "B15", "B16", "B17", "B18", "B19", "B20", "B21", "B22", "B23", "B24", "B25", "B26", "B27", "B28", "B29", "B30", "B31", "B32", "B33"};
    String[] studentBalanceCells = {"AF14", "AF15", "AF16", "AF17", "AF18", "AF19", "AF20", "AF21", "AF22", "AF23", "AF24", "AF25", "AF26", "AF27", "AF28", "AF29", "AF30", "AF31", "AF32", "AF33"};
//    String[] weekDaysOne = {"AO2","AP2","AQ2","AR2","AS2","AT2","AU2"};
//    String[] weekDaysTwo = {"AO4","AP4","AQ4","AR4","AS4","AT4","AU4"};
//    String[] indGraphicCells = {"H14","H15","H16","H17","H18","H19","H20","H21","H22","H23","H24","H25","H26","H27","H28","H29","H30","H31","H32","H33"};     
    String singleDiscountColumn = "M14";
    String permanentDiscountColumn = "O14";

    private int rowStartDebt = 0;

    @FXML
    private void chooseFileAction(ActionEvent event) {

        FileChooser fc = new FileChooser();

        fc.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));

        if (!savedPath.equals("")) {
            fc.setInitialDirectory(new File(savedPath));
        }

        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Файлы Excel", Arrays.asList("*.xl,","*.xlsx","*.xlsm","*.xlsb","*.xlam","*.xltx","*.xltm","*.xls","*.xlt")));
        File file = fc.showOpenDialog(null);

        if (file != null) {
            saveFilePath = file.getAbsolutePath();
            chosenFile = file.getAbsolutePath();
            fileName = file.getName().replaceAll(".xlsm", " - должники.xlsx");
            filePathTextField.setText(chosenFile);
            readyButton.setDisable(false);
            System.out.println("chosenFile: " + chosenFile);
        } else if (chosenFile.isEmpty()) {
            System.out.println("Файл не был выбран");
            filePathTextField.setText("Выберите файл");
            readyButton.setDisable(true);
        }
    }

    @FXML
    private void saveFileAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home") + File.separator +  "Desktop"));

        File file = dc.showDialog(null);

        if (file != null) {
            savedPath = file.getAbsolutePath();
            saveFilePathTextField.setText(savedPath);
            readyButton.setDisable(false);
        } else if (savedPath.isEmpty()) {
            System.out.println("Путь сохранения остался прежним");
            saveFilePathTextField.setText("Укажите путь сохранения файла");
        }
    }

    @FXML
    private void backAction(ActionEvent event) throws IOException {
        Parent next = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        Scene sceneTwo = new Scene(next);
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage) source.getScene().getWindow();
        thisStage.setScene(sceneTwo);
        thisStage.show();
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

    public double discountPayment(Student student, GroupInfo group) {
        double discountPay = 0.0;
        if (student.getDiscount() != 0) {
            discountPay = (group.getPricePerHour() - (group.getPricePerHour() / 100 * student.getDiscount())) * student.getBalance();
        } else {
            discountPay = group.getPricePerHour() * student.getBalance();
        }
        return discountPay;
    }

    private XSSFWorkbook createReport() {
        XSSFWorkbook report = new XSSFWorkbook();

        report.createSheet("Должники");

        return report;
    }

    public void createNEWExcelTable(XSSFWorkbook report, String sheetName, GroupInfo group) throws FileNotFoundException, IOException {

        XSSFSheet sheet = report.getSheet(sheetName);
        System.out.println("sheetName: " + sheet.getSheetName());

        int rowCount = rowStartDebt;
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
        subHeader.setBorderTop(BorderStyle.THIN);
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
        
        CellStyle sumDebtStyle = report.createCellStyle();
        sumDebtStyle.setAlignment(HorizontalAlignment.CENTER);
        sumDebtStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        sumDebtStyle.setBorderRight(BorderStyle.THIN);
        sumDebtStyle.setBorderBottom(BorderStyle.THIN);

        Row row = sheet.createRow(rowCount);
        cellCount = 0;
        Cell cell = row.createCell(cellCount);
        if (rowCount == 0) {
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, cellCount, cellCount + columnQuantity - 1));
            cell.setCellValue("Список должников");
            cell.setCellStyle(sheetHeader);
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
            cell.setCellValue("Долг \nв часах");
            cell.setCellStyle(subHeader);
            cell = row.createCell(++cellCount);
            cell.setCellValue("Долг \nв руб");
            cell.setCellStyle(subHeader);
        }

        /* Заполнение таблицы информацией о студентах */
        

        System.out.println("Кол-во студентов: " + group.getStudentsInfo().size());
        double debtSum = 0;
        
        for (Student student : group.getStudentsInfo()) {
            row = sheet.createRow(++rowCount);
            cellCount = 0;
            cell = row.createCell(cellCount);
            cell.setCellValue((row.getRowNum() - 1) + ")");
            cell.setCellStyle(leftBorder);

            cell = row.createCell(++cellCount);
            cell.setCellValue(student.getName());

            cell = row.createCell(++cellCount);
            if (student.getDiscount() != 0) {
                cell.setCellValue((int) student.getDiscount() + "%");
            }
            cell.setCellStyle(center);

            cell = row.createCell(++cellCount);
            cell.setCellValue(student.getBalance());
            cell.setCellStyle(payHour);

            cell = row.createCell(++cellCount);
            cell.setCellValue(String.format("%.2f", discountPayment(student, group)) + " руб");
            debtSum += Double.parseDouble(String.format("%.2f", discountPayment(student, group)).replaceAll(",", "."));
            cell.setCellStyle(rublePay);

            cell = row.createCell(++cellCount);
            cell.setCellStyle(leftBorder);

            RegionUtil.setBorderBottom(BorderStyle.DASHED, new CellRangeAddress(rowCount, rowCount, 0, cellCount - 1), sheet);
            rowStartDebt = row.getRowNum() + 1;
        }
        RegionUtil.setBorderBottom(BorderStyle.THIN, new CellRangeAddress(rowCount, rowCount, 0, cellCount - 1), sheet);
        
        row = sheet.createRow(++rowCount);
            cellCount = 3;
            cell = row.createCell(cellCount);
            cell.setCellStyle(subHeader);
            cell.setCellValue("Общая сумма \nзадолжностей");
            cell = row.createCell(++cellCount);
            cell.setCellValue(String.format("%.2f",debtSum) + " руб");
            cell.setCellStyle(sumDebtStyle);
            
            
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

    }

    @FXML
    private void readyAction(ActionEvent event) {
        pool = Executors.newWorkStealingPool();
        readyButton.setDisable(true);
        backButton.setDisable(true);
        chooseFileButton.setDisable(true);
        saveFileButton.setDisable(true);
        progressIndicator.setVisible(true);
        progressBar.setVisible(true);
        progressLabel.setVisible(true);

        XSSFWorkbook report = createReport();

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
                }
            }

        });

        pool.submit(() -> {

            try {

                File file = new File(chosenFile);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText("Запуск поиска...");
                    }
                });

                System.out.println("РАБОТАЮ С ФАЙЛОМ: " + chosenFile);
                XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(file));
                int sheetsNumber = workbook.getNumberOfSheets();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText("Сбор информации из файла " + fileName);
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
                    if (sheet.getSheetName().equals("ostatki")) {
                        continue;
                    }
                    if (sheet.getSheetName().equals("OS")) {
                        continue;
                    }
                    if (sheet.getSheetName().equals("оста")) {
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
                GroupInfo groupInfo = new GroupInfo();
                for (XSSFSheet sheetItem : sheets) {
                    synchronized (condition) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                progressLabel.setText("Обработка группы: " + sheetItem.getSheetName());
                            }
                        });

                        
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
                                    groupInfo.setPricePerHour(Double.parseDouble(cellItem.getValue()));
                                    break;
                                case ("A3"):
                                    groupInfo.setGroupId(cellItem.getValue());
                                    break;
                                case ("Y1"):
                                    groupInfo.setClassDurationOne(Double.parseDouble(cellItem.getValue()));
                                    break;
                                case ("Y3"):
                                    groupInfo.setClassDurationTwo(Double.parseDouble(cellItem.getValue()));
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

                            if (student.getBalance() <= -0.01) {
                                groupInfo.addStudent(student);
                            }
                        }

                        /* Объект класса GroupInfo сформирован */
                        
                        progress.add(percent);

//                        for (Student student : groupInfo.getStudentsInfo()) {
//                            student = null;
//                        }
                        //groupInfo.getStudentsInfo().clear();
                        condition.setProgressTime(true);
                        condition.notify();
                        condition.wait();
                    }

                    //System.out.println(groupInfo);
                    //System.out.println();
                }
                createNEWExcelTable(report, "Должники", groupInfo);
                //progress.add(0.2);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText("Обработка завершена");
                    }
                });

                System.out.println("Обработка завершена");

                progressBar.setProgress(1.0);
                progressIndicator.setProgress(1.0);

                //openReportButton.setVisible(true);
                try (FileOutputStream out = new FileOutputStream(new File(saveFilePathTextField.getText()) + File.separator + fileName)) {
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
                openFileButton.setVisible(true);
                System.out.println("Excel файл успешно создан!");
                System.out.println(saveFilePathTextField.getText());
                //finished.setVisible(true);
                //progress.add(0.2);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
            //System.out.println("Поток обработки данных жив? " + Thread.currentThread().isAlive());
        });
    }
    
    @FXML
    private void openFileAction(ActionEvent event){
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(new File(saveFilePathTextField.getText() + File.separator + fileName));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        saveFilePathTextField.setText(System.getProperty("user.home")+  File.separator + "Desktop");
        readyButton.setDisable(true);
        progressIndicator.setMinHeight(68.0);
        progressIndicator.setMinWidth(101.0);
        progressIndicator.setVisible(false);
        progressBar.setVisible(false);
        progressLabel.setVisible(false);
        openFileButton.setVisible(false);
        progressLabel.setText("");
    }

}
