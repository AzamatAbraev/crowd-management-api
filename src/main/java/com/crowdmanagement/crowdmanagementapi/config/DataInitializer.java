package com.crowdmanagement.crowdmanagementapi.config;

import com.crowdmanagement.crowdmanagementapi.building.Building;
import com.crowdmanagement.crowdmanagementapi.building.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BuildingRepository buildingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (buildingRepository.count() == 0) {

            List<Building> initialBuildings = List.of(
                    new Building("LRC", "Learning Resource Center", "Main Library of the University"),
                    new Building("ATB", "Amir Temur Building", "One of the main building where classes are held"),
                    new Building("SHB", "Shakhrisabz Building", "One of the main building where classes are held"),
                    new Building("IB", "Istiqbol Building", "The oldest building in the campus"),
                    new Building("Lyceum", "Academic Lyceum of Westminster University", "Mostly BIS and CS classes takes place")
            );

            buildingRepository.saveAll(initialBuildings);
            System.out.println("Fixed buildings array loaded into the database.");
        }
    }
}
