package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.StageUtils;
import utils.Storage;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HomeController extends BaseController implements Initializable {
    @FXML
    private TableView<Employee> tableView;

    @FXML
    private TableColumn<Employee, String> clName;

    @FXML
    private TableColumn<Employee, String> clId;

    @FXML
    private TableColumn<Employee, String> clGender;

    @FXML
    private TableColumn<Employee, String> clPhoneNumber;

    @FXML
    private TableColumn<Employee, String> clAddress;

    @FXML
    private TableColumn<Employee, String> clEmail;

    @FXML
    private TableColumn<Employee, String> clDateOfBirth;

    @FXML
    private TableColumn<Employee, String> clDayBeginWork;

    @FXML
    private TextField txtDateOfBirth;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtDayBegin;

    @FXML
    RadioButton male;

    @FXML
    RadioButton female;

    @FXML
    RadioButton allGender;

    private ToggleGroup toggleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        male.setUserData("Nam");
        female.setUserData("Nữ");
        allGender.setUserData("All");
        allGender.setSelected(true);
        toggleGroup = new ToggleGroup();
        male.setToggleGroup(toggleGroup);
        female.setToggleGroup(toggleGroup);
        allGender.setToggleGroup(toggleGroup);
        clId.setCellValueFactory(new PropertyValueFactory<>("id"));
        clName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        clPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        clAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        clEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        clDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        clDayBeginWork.setCellValueFactory(new PropertyValueFactory<>("dayBeginWork"));
        tableView.setItems(FXCollections.observableArrayList(Storage.getListEmployees()));
    }

    public void addEmployee() {
        Employee e = new Employee();
        e.setId(txtId.getText());
        e.setName(txtName.getText());
        e.setGender((String) toggleGroup.getSelectedToggle().getUserData());
        e.setPhoneNumber(txtPhoneNumber.getText());
        e.setAddress(txtAddress.getText());
        e.setEmail(txtEmail.getText());
        e.setDateOfBirth(txtDateOfBirth.getText());
        e.setDayBeginWork(txtDayBegin.getText());
        Storage.add(e, tableView);
        clear();
    }

    public void clear() {
        txtId.clear();
        txtName.clear();
        txtPhoneNumber.clear();
        txtAddress.clear();
        txtEmail.clear();
        txtDateOfBirth.clear();
        txtDayBegin.clear();
    }

    public void save() {
        Storage.save();
    }

    public void delete() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        Storage.delete(selected, tableView);
    }

    public void findPerson() {
        ObservableList<Employee> list = FXCollections.observableArrayList(Storage.getListEmployees().stream().filter(employee -> {
            boolean nameCorrect = true;
            boolean idCorrect = true;
            boolean genderCorrect = true;
            boolean phoneCorrect = true;
            if (!isEmpty(txtId.getText())) {
                idCorrect = txtId.getText().equals(employee.getId());
            }
            if (!isEmpty(txtName.getText())) {
                nameCorrect = employee.getName().contains(txtName.getText());
            }
            if (!isEmpty((String) toggleGroup.getSelectedToggle().getUserData())) {
                genderCorrect = employee.getGender().equals(toggleGroup.getSelectedToggle().getUserData());
            }
            if ("All".equals(toggleGroup.getSelectedToggle().getUserData())) {
                genderCorrect = true;
            }
            if (!isEmpty(txtPhoneNumber.getText())) {
                phoneCorrect = employee.getPhoneNumber().equals(txtPhoneNumber.getText());
            }
            return nameCorrect && idCorrect && genderCorrect && phoneCorrect;
        }).collect(Collectors.toList()));
        tableView.setItems(list);
    }

    private boolean isEmpty(String val) {
        return "".equals(val.trim());
    }

    public void goBack() {
        StageUtils.openView("../view/home.fxml", true);
    }

    public void edit(ActionEvent event) throws IOException {
        Employee e = tableView.getSelectionModel().getSelectedItem();
        StageUtils.openDialog("edit.fxml", "Edit", e, this);
    }

    public void wage(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/wage.fxml"));
        Parent editView = loader.load();
        Scene scene = new Scene(editView);
        WageController ctrl = loader.getController();
        Employee e = tableView.getSelectionModel().getSelectedItem();
        ctrl.setEmployee(e);
        stage.setScene(scene);
    }

    @Override
    public void closeEvent() {
        findPerson();
        tableView.refresh();
    }
}
