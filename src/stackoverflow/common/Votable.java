package stackoverflow.common;

import stackoverflow.entity.Vote;

public interface Votable {
    void addVote(Vote vote);
}
