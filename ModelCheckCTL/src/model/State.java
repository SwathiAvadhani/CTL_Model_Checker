package model;

import java.util.LinkedHashSet;
import java.util.Set;

public class State {
    public String stateName;
    public Set<String> atoms;

    public State(String stateName) {
        this.stateName = stateName;
        this.atoms = new LinkedHashSet<>();
    }
    public State(String stateName, Kripke _kripke) {
        for(State state: _kripke._state){
            if(state.stateName.equals(stateName)){
                this.stateName = state.stateName;
                this.atoms = state.atoms;
            }
        }
    }

    /**
     * Equals method is implemented
     * @param obj It is object
     * @return If true
     */
    public boolean equals(Object obj) {
        // If the object is compared with itself then return true
        if (obj == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof State)) {
            return false;
        }

        State other = (State) obj;
        if (this.stateName.equals(other.stateName)) {
            return true;
        }
        return false;
    }
}