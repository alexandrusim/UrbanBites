package gio.backend.service;

import gio.backend.entity.Table;
import gio.backend.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TableService {

    @Autowired
    private TableRepository tableRepository;
    
    @Autowired
    private SecurityService securityService;

    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }

    public Optional<Table> getTableById(Integer id) {
        return tableRepository.findById(id);
    }

    public List<Table> getTablesByRestaurantId(Integer restaurantId) {
        return tableRepository.findByRestaurantId(restaurantId);
    }

    public List<Table> getAvailableTablesByRestaurant(Integer restaurantId) {
        return tableRepository.findByRestaurantIdAndIsAvailable(restaurantId, true);
    }

    public Table createTable(Table table) {
        if (!securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(table.getRestaurantId());
        }
        
        return tableRepository.save(table);
    }

    public Table updateTable(Integer id, Table tableDetails) {
        return tableRepository.findById(id)
                .map(table -> {
                    if (!securityService.isSystemAdmin()) {
                        securityService.checkRestaurantAccess(table.getRestaurantId());
                    }
                    
                    if (tableDetails.getTableNumber() != null) {
                        table.setTableNumber(tableDetails.getTableNumber());
                    }
                    if (tableDetails.getCapacity() != null) {
                        table.setCapacity(tableDetails.getCapacity());
                    }
                    if (tableDetails.getLocation() != null) {
                        table.setLocation(tableDetails.getLocation());
                    }
                    if (tableDetails.getIsAvailable() != null) {
                        table.setIsAvailable(tableDetails.getIsAvailable());
                    }
                    if (tableDetails.getPositionX() != null) {
                        table.setPositionX(tableDetails.getPositionX());
                    }
                    if (tableDetails.getPositionY() != null) {
                        table.setPositionY(tableDetails.getPositionY());
                    }
                    if (tableDetails.getShape() != null) {
                        table.setShape(tableDetails.getShape());
                    }
                    if (tableDetails.getNotes() != null) {
                        table.setNotes(tableDetails.getNotes());
                    }
                    return tableRepository.save(table);
                })
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
    }

    public void deleteTable(Integer id) {
        tableRepository.deleteById(id);
    }
}
