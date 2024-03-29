package com.tendersearch.backend;

import com.tendersearch.backend.models.Tender;
import com.tendersearch.backend.repositories.TenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
public class TenderController {
    private TenderRepository tenderRepository;

    @Autowired
    public void setTenderRepository(TenderRepository tenderRepository) {
        this.tenderRepository = tenderRepository;
    }

    @GetMapping("/tender")
    public List<TenderShort> tenders(@RequestParam @Nullable Integer page, @RequestBody @Nullable TenderFilter filter) {
        int value = 0;
        if (page != null) {
            value = page;
        }
        if (filter != null) {
            var result = new ArrayList<TenderShort>();
            int page_local = 0;
            while (result.size() < 10) {
                var entities = tenderRepository.findAll(PageRequest.of(page_local, 10));
                if (entities.getSize() == 0) {
                    break;
                }
                result.addAll(entities.stream().filter(
                        entity -> {
                            var shouldBeIncluded = true;
                            if (filter.keywords != null) {
                                shouldBeIncluded = entity.name.contains(filter.keywords);
                            }
                            if (filter.createdAt != null && shouldBeIncluded) {
                                shouldBeIncluded = entity.createdAt.isBefore(filter.createdAt);
                            }
                            if (filter.closedAt != null && shouldBeIncluded) {
                                shouldBeIncluded = entity.closedAt.isAfter(filter.closedAt);
                            }
                            if (filter.customer != null && shouldBeIncluded) {
                                shouldBeIncluded = entity.customer.contains(filter.customer);
                            }
                            return shouldBeIncluded;
                        }
                ).map(TenderShort::fromTenderDB).toList());
                page_local++;
            }
            return result.subList(value*10, (value+1)*10);
        }
        return tenderRepository.findAll().stream().map(TenderShort::fromTenderDB).toList();
    }

    @GetMapping("/tender/{id}")
    public Tender tenderById(@PathVariable int id) {
        return tenderRepository.findById((long) id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tender was not found"));
    }

    @PostMapping("/tender")
    public Tender updateTender(@RequestBody Tender tender) {
        Optional<Tender> existing = tenderRepository.findByvKey(tender.vKey).stream().findFirst();
        existing.ifPresent(value -> tender.id = value.id);
        return tenderRepository.save(tender);
    }
}
