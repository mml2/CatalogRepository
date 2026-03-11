package main.config;

import model.Category;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import repository.CategoryRepository;

@Component
public class CategoryDataLoader implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    public CategoryDataLoader(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        saveCategoryIfMissing("Electronics");
        saveCategoryIfMissing("Fashion");
    }

    private void saveCategoryIfMissing(String name) {
        if (categoryRepository.findByName(name).isEmpty()) {
            categoryRepository.save(new Category(name));
        }
    }
}
