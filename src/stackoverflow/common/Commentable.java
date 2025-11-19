package stackoverflow.common;


import stackoverflow.entity.Comment;

//必须是常量： 所有在接口中声明的变量都会被隐式地视为 public static final（公共、静态、最终的常量）
public interface Commentable {

    void addComment(Comment comment);
}
