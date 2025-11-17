package stackoverflow;

import java.util.Objects;

//  Tag 类没有重写 equals() 和 hashCode()，导致：
//  - 作为 Map key 时会出现重复
public class Tag {
    private final String name;

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    @Override
    public boolean equals(Object tag) {
        //1. 检查自反性
        if (this == tag) {
            return true;
        }

        //2. 检查null和类型
        if (tag == null || getClass() != tag.getClass()) {
            return false;
        }

        // 3. 类型转换
        Tag other = (Tag) tag;

        // 4. 比较关键字段
        return Objects.equals(name, other.name);

    }


}
