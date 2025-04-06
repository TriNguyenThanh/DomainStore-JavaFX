package com.utc2.domainstore.ThanhTri;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class TableViewTest extends Application {

    public static class Person {
        private String name;
        private int age;
        private String gender;
        private String address;

        public Person(String name, int age, String gender, String address) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return String.format("Person: %s, %02d, %s, %s", name, age, gender, address);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        TableView<Person> table = new TableView<>();

        TableColumn<Person, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Person, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Person, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Person, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        table.getColumns().add(nameColumn);
        table.getColumns().add(ageColumn);
        table.getColumns().add(genderColumn);
        table.getColumns().add(addressColumn);

        ObservableList<Person> data = FXCollections.observableArrayList(
                new Person("John Doe", 30, "Male", "123 Main St"),
                new Person("Jane Smith", 25, "Female", "456 Maple Ave"),
                new Person("Mike Johnson", 40, "Male", "789 Oak Dr")
        );

        table.setItems(data);

        Scene scene = new Scene(table, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Person Table View");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}