module org.icesi.implementacionintegradora {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.icesi.implementacionintegradora to javafx.fxml;
    exports org.icesi.implementacionintegradora;
    exports org.icesi.implementacionintegradora.controllers;
    opens org.icesi.implementacionintegradora.controllers to javafx.fxml;
}