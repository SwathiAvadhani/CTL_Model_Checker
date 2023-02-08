package model;


import java.util.*;

public class Kripke {

    public Set<State> _state;
    public Set<String> _atom;
    public List<Transition> _transition;

    //Parameterized constructor
    public Kripke(String kripkeStructureDefinition) {

        //contructor
        _state = new LinkedHashSet<>();
        _atom = new LinkedHashSet<>();
        _transition = new ArrayList<>();


        try {
            //Parsing Kripke Structure
            List<String> parsedStructure = new ArrayList<String>(Arrays.asList(kripkeStructureDefinition.replaceAll("[\\t\\n\\r]+"," ").split(";")));
            int i = 0;
            while (i < parsedStructure.size()) {
                System.out.println("Parsed structure " +  i  + " " + parsedStructure.get(i));
                i++;
            }
            System.out.println("Whole parsed structure : " + parsedStructure.toString());

            if (parsedStructure == null || parsedStructure.size() != 3)
                throw new IllegalArgumentException("Input file does not contain appropriate segments to construct kripke structure");


            List<String> stateNames = Arrays.asList(parsedStructure.get(0)
                    .replace(" ", "")
                    .split(","));
            //System.out.println("State names : " + stateNames.toString());

            List<String> transitions = Arrays.asList(parsedStructure.get(1)
                    .replace(" ", "")
                    .split(","));
           // System.out.println("Transitions : " + transitions.toString());

            List<String> stateAtomStructures = Arrays.asList(parsedStructure.get(2)
                    .split(","));
          //  System.out.println("State Atom Structures : " + stateAtomStructures.toString());

            //load states
            System.out.println("Load states....");
            for (String stateName : stateNames) {
                State state = new State(stateName);
                if (!_state.contains(state))
                    _state.add(new State(stateName));
                else
                    throw new IllegalArgumentException(String.format("State %s is defined more than once", stateName));
            }

            //load transitions
            System.out.println("Load transitions....");
            for (String transition : transitions) {
                List<String> parsedTransition = Arrays.asList(transition.split(":"));

                if (parsedTransition == null || parsedTransition.size() != 2)
                    throw new IllegalArgumentException("Transition is not in the valid format");

                String transitionName = parsedTransition.get(0);
                List<String> parsedFromToStates = Arrays.asList(parsedTransition.get(1).split("-"));

                if (parsedFromToStates == null || parsedFromToStates.size() != 2)
                    throw new IllegalArgumentException(String.format("Transition %s is not in [from state] - [to state] format", transitionName));

                String fromStateName = parsedFromToStates.get(0);
                String toStateName = parsedFromToStates.get(1);
                State fromState = findStateByName(fromStateName);
                State toState = findStateByName(toStateName);

                if (fromState == null || toState == null)
                    throw new IllegalArgumentException(String.format("Invalid state is detected in transition %s", transitionName));

                Transition transitionObj = new Transition(transitionName, fromState, toState);
                if (!this._transition.contains(transitionObj))
                    this._transition.add(transitionObj);
                else {
                    throw new IllegalArgumentException(String.format("Transitions from state %s to state %s are defined more than once"
                            , fromStateName, toStateName));
                }
            }

            //load atoms
            System.out.println("Load atoms...");
            for (String stateAtomStructure : stateAtomStructures) {
                List<String> parsedStateAtomStructure = Arrays.asList(stateAtomStructure.split(":"));

                if (parsedStateAtomStructure == null || parsedStateAtomStructure.size() !=2)
                    throw new IllegalArgumentException(String.format("%s is not a valid state: atoms definition", stateAtomStructure));
                String stateName = parsedStateAtomStructure.get(0).replace(" ", "");
                String atomNames = parsedStateAtomStructure.get(1).trim();
                List<String> parsedAtoms = Arrays.asList(atomNames.split(" "));

                Set<String> stateAtoms = new LinkedHashSet<>();
                for (String atom : parsedAtoms) {
                    if (atom == null && atom.length() == 0) {
                    } else if (!stateAtoms.contains(atom))
                        stateAtoms.add(atom);
                    else
                        throw new IllegalArgumentException(String.format("Atom %s is defined more than once for state %s"
                                , atom, stateName));
                }

                State stateObj = findStateByName(stateName);
                if (stateObj == null)
                    throw new IllegalArgumentException(String.format("State %s is not defined", stateName));
                stateObj.atoms = stateAtoms;

                //load to list of atoms
                for (String atom : stateAtoms) {
                    if (!_atom.contains(atom))
                        _atom.add(atom);
                }
            }
        } catch (IllegalArgumentException fe) {
            throw fe;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid kripke input structure");
        }
    }

    public List<String> getAllStates() {
        List<String> ls = new ArrayList<>();
        for (State state: _state) {
            ls.add(state.stateName);
        }
        return ls;
    }

    /// <summary>
    /// Find the state by its name
    /// </summary>
    /// <param name="stateName"></param>
    /// <returns></returns>
    public State findStateByName(String stateName) {
        for (State state : _state) {
            if (state.stateName.equals(stateName))
                return state;
        }

        return null;
    }

    /// <summary>
    /// Override ToString method
    /// </summary>
    /// <returns></returns>
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("STATES\n");
        sb.append("-----------\n");
        sb.append(statesToString());
        sb.append("\n");
        sb.append("\n");
        sb.append("TRANSITIONS\n");
        sb.append("-------------------\n");
        sb.append(transitionsToString());

        return sb.toString();
    }

    public String statesToString() {
        StringBuilder sb = new StringBuilder();

        List<String> stateStrings = new ArrayList<String>();
        for (State state : _state) {
            String atomNames = String.join(", ", state.atoms);
            stateStrings.add(String.format("%s(%s)", state.stateName, atomNames));
        }

        sb.append(String.join(", ", stateStrings));
        return sb.toString();
    }

    public String transitionsToString() {
        StringBuilder sb = new StringBuilder();

        List<String> transitionString = new ArrayList<String>();
        for (Transition transition : _transition) {
            transitionString.add(String.format("%s(%s -> %s)", transition.transitionName
                    , transition.fromState.stateName, transition.toState.stateName));
        }

        sb.append(String.join(", ", transitionString));
        return sb.toString();
    }


}

