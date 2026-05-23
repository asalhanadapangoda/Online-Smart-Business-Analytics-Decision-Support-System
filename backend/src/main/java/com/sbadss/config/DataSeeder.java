package com.sbadss.config;

import com.sbadss.entity.Branch;
import com.sbadss.entity.Role;
import com.sbadss.entity.User;
import com.sbadss.entity.Category;
import com.sbadss.entity.Product;
import com.sbadss.entity.Customer;
import com.sbadss.repository.BranchRepository;
import com.sbadss.repository.RoleRepository;
import com.sbadss.repository.UserRepository;
import com.sbadss.repository.CategoryRepository;
import com.sbadss.repository.ProductRepository;
import com.sbadss.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedDemoData() {
        return args -> {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(createRole("ADMIN", "System administrator")));
            Role managerRole = roleRepository.findByName("MANAGER")
                    .orElseGet(() -> roleRepository.save(createRole("MANAGER", "Branch manager")));
            Role cashierRole = roleRepository.findByName("CASHIER")
                    .orElseGet(() -> roleRepository.save(createRole("CASHIER", "Point of sale operator")));

            Branch mainBranch = branchRepository.findByName("Main Branch")
                    .orElseGet(() -> {
                        Branch branch = new Branch();
                        branch.setName("Main Branch");
                        branch.setLocation("Head Office");
                        branch.setContactNumber("+0000000000");
                        branch.setActive(true);
                        return branchRepository.save(branch);
                    });

            createUserIfMissing("admin", "admin@sbadss.local", "System Admin", "admin123", adminRole, mainBranch);
            createUserIfMissing("manager", "manager@sbadss.local", "Branch Manager", "manager123", managerRole, mainBranch);
            createUserIfMissing("cashier", "cashier@sbadss.local", "Cashier User", "cashier123", cashierRole, mainBranch);

            // Seed exactly 10 branches (Town Branch, Town, Phone)
            seedBranches();

            // Seed exactly 50 products within 10 categories
            seedProductsAndCategories(mainBranch);

            // Seed exactly 20 customers
            seedCustomers(mainBranch);

            log.info("Demo seed data ensured (roles, branches, users, categories, products, customers).");
        };
    }

    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return role;
    }

    private void createUserIfMissing(
            String username,
            String email,
            String fullName,
            String rawPassword,
            Role role,
            Branch branch
    ) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setBranch(branch);
        user.setActive(true);
        userRepository.save(user);
    }

    private void seedBranches() {
        if (branchRepository.count() > 1) { // 1 is mainBranch
            log.info("Branches already seeded. Skipping branch seeder.");
            return;
        }

        // Define 10 Sri Lankan commercial towns and distinct contact numbers
        String[][] branchData = {
            {"Colombo", "+94112345671"},
            {"Kandy", "+94812345672"},
            {"Galle", "+94912345673"},
            {"Negombo", "+94312345674"},
            {"Jaffna", "+94212345675"},
            {"Kurunegala", "+94372345676"},
            {"Gampaha", "+94332345677"},
            {"Trincomalee", "+94262345678"},
            {"Anuradhapura", "+94252345679"},
            {"Batticaloa", "+94652345680"}
        };

        for (String[] bData : branchData) {
            String town = bData[0];
            String phone = bData[1];
            String branchName = town + " Branch";

            if (!branchRepository.existsByName(branchName)) {
                Branch branch = new Branch();
                branch.setName(branchName);
                branch.setLocation(town);
                branch.setContactNumber(phone);
                branch.setActive(true);
                branchRepository.save(branch);
            }
        }
        log.info("10 Branches seeded successfully.");
    }

    private void seedProductsAndCategories(Branch branch) {
        if (productRepository.count() > 0) {
            log.info("Products already seeded. Skipping product seeder.");
            return;
        }

        // Define 10 categories and their 5 products each (name, description, price, purchasePrice, stock, sku)
        String[][] data = {
            {"Beverages", "Refreshing drinks and hot beverages", 
             "Cola Soda", "Classic cola soda 12oz", "1.50", "0.80", "120", "BEV-001",
             "Orange Juice", "Freshly squeezed orange juice 32oz", "2.99", "1.50", "80", "BEV-002",
             "Dark Roast Coffee", "Organic whole bean dark roast coffee bag", "8.99", "4.50", "45", "BEV-003",
             "Green Tea", "Premium green tea bags box of 20", "3.49", "1.80", "60", "BEV-004",
             "Energy Drink", "High caffeine energy boost can", "2.49", "1.10", "100", "BEV-005"},
            
            {"Snacks", "Crispy chips, crackers, and quick bites", 
             "Potato Chips", "Classic salted crispy potato chips bag", "1.99", "0.90", "150", "SNC-001",
             "Salted Pretzels", "Traditional crunchy salted pretzels bag", "2.29", "1.10", "90", "SNC-002",
             "Butter Popcorn", "Microwave butter popcorn 3-pack", "3.99", "1.80", "70", "SNC-003",
             "Tortilla Chips", "Organic yellow corn tortilla chips bag", "2.49", "1.20", "110", "SNC-004",
             "Mixed Nuts", "Roasted and salted mix of almonds and cashews", "6.99", "3.50", "50", "SNC-005"},
            
            {"Bakery", "Freshly baked breads, pastries, and muffins", 
             "Sliced White Bread", "Freshly baked white sandwich bread loaf", "2.49", "1.00", "40", "BAK-001",
             "Whole Wheat Bread", "Nutritious stone ground whole wheat bread", "2.99", "1.20", "35", "BAK-002",
             "Chocolate Muffin", "Rich double chocolate chip bakery muffin", "1.89", "0.75", "50", "BAK-003",
             "Butter Croissant", "Flaky and buttery golden croissant", "2.19", "0.90", "30", "BAK-004",
             "Plain Bagels", "Freshly baked plain bagels 6-pack bag", "3.49", "1.60", "25", "BAK-005"},
            
            {"Dairy", "Milk, cheeses, butter, and yogurts", 
             "Whole Milk 1G", "Pasteurized whole milk 1 gallon jug", "3.89", "2.10", "45", "DRY-001",
             "Cheddar Cheese Block", "Sharp yellow cheddar cheese block 8oz", "4.29", "2.30", "60", "DRY-002",
             "Greek Yogurt 32oz", "Plain unsweetened Greek yogurt tub", "5.49", "3.00", "30", "DRY-003",
             "Salted Butter", "Premium salted creamery butter 1lb box", "4.99", "2.60", "55", "DRY-004",
             "Heavy Whipping Cream", "Fresh heavy whipping cream 16oz carton", "2.99", "1.50", "40", "DRY-005"},
            
            {"Produce", "Fresh organic vegetables and farm fruits", 
             "Organic Bananas", "Fresh organic sweet yellow bananas 1lb bundle", "0.69", "0.30", "200", "PRD-001",
             "Red Apples 3lb", "Sweet crispy red delicious apples bag", "4.49", "2.00", "75", "PRD-002",
             "Fresh Strawberries", "Organic sweet ripe strawberries 1lb clamshell", "3.99", "1.80", "50", "PRD-003",
             "Baby Spinach 1lb", "Pre-washed organic baby spinach container", "3.49", "1.60", "60", "PRD-004",
             "Baby Carrots 1lb", "Pre-peeled crispy baby carrots snack bag", "1.49", "0.70", "80", "PRD-005"},
            
            {"Frozen Foods", "Frozen quick meals, vegetables, and treats", 
             "Vanilla Ice Cream 1.5qt", "Rich creamy vanilla bean premium ice cream tub", "4.99", "2.40", "40", "FRZ-001",
             "Frozen Pepperoni Pizza", "Classic thin crust pepperoni stone-oven pizza", "6.49", "3.20", "50", "FRZ-002",
             "Frozen Waffles", "Home-style crispy toaster waffles 10-pack box", "2.99", "1.40", "65", "FRZ-003",
             "Frozen Green Peas 1lb", "Sweet organic green peas steam bag", "1.89", "0.90", "90", "FRZ-004",
             "Frozen French Fries 2lb", "Crispy straight cut classic french fries bag", "3.29", "1.50", "80", "FRZ-005"},
            
            {"Pantry", "Essential cooking dry goods and oils", 
             "Spaghetti Pasta 1lb", "Premium semolina spaghetti pasta box", "1.29", "0.55", "120", "PAN-001",
             "Extra Virgin Olive Oil", "Cold pressed extra virgin olive oil 17oz bottle", "8.99", "4.80", "40", "PAN-002",
             "Canned Peeled Tomatoes", "Organic whole peeled plum tomatoes 28oz can", "2.19", "0.95", "100", "PAN-003",
             "Classic Marinara Sauce", "Traditional Italian herb marinara sauce jar", "2.79", "1.30", "80", "PAN-004",
             "White Jasmine Rice 5lb", "Premium long grain white jasmine rice bag", "4.99", "2.20", "70", "PAN-005"},
            
            {"Meat & Seafood", "Fresh cut quality meats and seafood", 
             "Chicken Breasts 2lb", "Boneless skinless fresh chicken breasts pack", "8.99", "4.50", "30", "MET-001",
             "Lean Ground Beef 1lb", "93% lean 7% fat organic ground beef pack", "6.49", "3.50", "45", "MET-002",
             "Atlantic Salmon Fillet", "Fresh wild-caught salmon fillet 1lb cut", "12.99", "7.00", "20", "MET-003",
             "Smoked Bacon 1lb", "Applewood thick-cut smoked bacon pack", "5.99", "3.00", "50", "MET-004",
             "Center Cut Pork Chops", "Bone-in pork chops 1.5lb family pack", "7.49", "3.80", "25", "MET-005"},
            
            {"Canned Goods", "Canned preserves, beans, and soups", 
             "Canned Albacore Tuna", "Wild caught chunk white tuna in water can", "1.89", "0.85", "110", "CAN-001",
             "Organic Black Beans", "Canned black beans low sodium 15oz can", "1.19", "0.50", "150", "CAN-002",
             "Sweet Canned Corn", "Super sweet whole kernel gold corn 15oz can", "0.99", "0.40", "160", "CAN-003",
             "Canned Sliced Peaches", "Sliced yellow peaches in light syrup 15oz can", "1.89", "0.80", "90", "CAN-004",
             "Chicken Noodle Soup", "Traditional condensed chicken noodle soup can", "1.49", "0.65", "100", "CAN-005"},
            
            {"Household", "Cleaning products and paper goods", 
             "Paper Towels 6-Roll", "Strong ultra-absorbent paper towels pack", "7.99", "4.00", "50", "HSD-001",
             "Liquid Dish Soap 20oz", "Degreasing citrus dish soap premium bottle", "2.49", "1.10", "80", "HSD-002",
             "Laundry Detergent 92oz", "Liquid laundry detergent original scent 64-loads", "12.99", "6.50", "35", "HSD-003",
             "Drawstring Garbage Bags", "Heavy duty 13-gallon kitchen garbage bags 40-pack", "8.49", "4.20", "60", "HSD-004",
             "Antibacterial Hand Soap", "Moisturizing hand soap pump 7.5oz bottle", "1.99", "0.85", "100", "HSD-005"}
        };

        for (String[] catData : data) {
            String catName = catData[0];
            String catDesc = catData[1];

            Category category = categoryRepository.findByName(catName)
                    .orElseGet(() -> {
                        Category newCatObj = new Category();
                        newCatObj.setName(catName);
                        newCatObj.setDescription(catDesc);
                        return categoryRepository.save(newCatObj);
                    });

            for (int i = 2; i < catData.length; i += 6) {
                String pName = catData[i];
                String pDesc = catData[i + 1];
                BigDecimal pPrice = new BigDecimal(catData[i + 2]);
                BigDecimal pPurchPrice = new BigDecimal(catData[i + 3]);
                Integer pQty = Integer.parseInt(catData[i + 4]);
                String pSku = catData[i + 5];

                Product product = new Product();
                product.setName(pName);
                product.setSku(pSku);
                product.setDescription(pDesc);
                product.setPrice(pPrice);
                product.setPurchasePrice(pPurchPrice);
                product.setStockQuantity(pQty);
                product.setMinThreshold(5);
                product.setCategory(category);
                product.setBranch(branch);
                product.setActive(true);
                productRepository.save(product);
            }
        }
        log.info("50 Products across 10 Categories seeded successfully.");
    }

    private void seedCustomers(Branch branch) {
        if (customerRepository.count() > 0) {
            log.info("Customers already seeded. Skipping customer seeder.");
            return;
        }

        // Define 20 customers (name, email, phoneNumber, address, loyaltyPoints)
        String[][] customerData = {
            {"Liam Smith", "liam.smith@example.com", "0711122334", "12 Maple Street, Colombo", "150"},
            {"Olivia Johnson", "olivia.j@example.com", "0722233445", "45 Oak Avenue, Kandy", "320"},
            {"Noah Williams", "noah.w@example.com", "0755566778", "78 Pine Lane, Galle", "90"},
            {"Emma Brown", "emma.b@example.com", "0777788990", "102 Birch Road, Negombo", "410"},
            {"Oliver Jones", "oliver.j@example.com", "0700011223", "23 Elm Court, Jaffna", "180"},
            {"Ava Garcia", "ava.g@example.com", "0766677889", "56 Cedar Drive, Kurunegala", "250"},
            {"Elijah Miller", "elijah.m@example.com", "0788899001", "89 Redwood Boulevard, Matara", "300"},
            {"Charlotte Davis", "charlotte.d@example.com", "0711223344", "34 Sequoia Way, Gampaha", "120"},
            {"William Rodriguez", "william.r@example.com", "0722334455", "67 Cypress Circle, Trincomalee", "60"},
            {"Sophia Martinez", "sophia.m@example.com", "0733445566", "90 Alder Place, Anuradhapura", "470"},
            {"James Hernandez", "james.h@example.com", "0744556677", "11 Chestnut Hill, Batticaloa", "210"},
            {"Isabella Lopez", "isabella.l@example.com", "0755667788", "44 Fir Ridge, Ratnapura", "130"},
            {"Benjamin Gonzalez", "benjamin.g@example.com", "0766778899", "77 Willow Brook, Kalutara", "80"},
            {"Mia Wilson", "mia.w@example.com", "0777889900", "99 Spruce Trail, Badulla", "520"},
            {"Lucas Anderson", "lucas.a@example.com", "0788990011", "15 Poplar Springs, Nuwara Eliya", "170"},
            {"Evelyn Thomas", "evelyn.t@example.com", "0799001122", "38 Aspen Valley, Hambantota", "290"},
            {"Alexander Taylor", "alexander.t@example.com", "0700112233", "61 Hickory Crest, Chilaw", "340"},
            {"Harper Moore", "harper.m@example.com", "0711335577", "84 Sycamore Grove, Ampara", "95"},
            {"Michael Jackson", "michael.j@example.com", "0722446688", "107 Beechwood Rise, Kegalle", "600"},
            {"Amelia Martin", "amelia.m@example.com", "0733557799", "130 Hemlock Park, Vavuniya", "230"}
        };

        for (String[] c : customerData) {
            String name = c[0];
            String email = c[1];
            String phone = c[2];
            String addr = c[3];
            Integer points = Integer.parseInt(c[4]);

            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhoneNumber(phone);
            customer.setAddress(addr);
            customer.setLoyaltyPoints(points);
            customer.setBranch(branch);
            customer.setActive(true);
            customerRepository.save(customer);
        }
        log.info("20 Customers seeded successfully.");
    }
}
