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
                    new Building("LRC-BUILDING", "Learning Resource Center", "Main Library of the University"),
                    new Building("ATB-BUILDING", "Amir Temur Building (ATB)", "One of the main building where classes are held"),
                    new Building("IB-BUILDING", "Istiqbol Building (IB)", "The oldest building in the campus"),
                    new Building("SHB-BUILDING", "Shakhrisabz Building (SHB)", "Mostly BIS and CS classes takes place")
            );

            buildingRepository.saveAll(initialBuildings);
            System.out.println("Fixed buildings array loaded into the database.");
        }
    }
}
