package payten.bot.model;

public class MenuItem {
    private String name;
    private Double price;
    private String description;
    private Integer time;
    private String category;

    public MenuItem() {
        // Default constructor required by Jackson
    }

    public MenuItem(String name, Double price, String description, Integer time, String category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.time = time;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }


}
