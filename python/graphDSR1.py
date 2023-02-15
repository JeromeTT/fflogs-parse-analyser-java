from numpy import dot
import pandas as pd
import csv
import matplotlib.pyplot as plt
import os
from numpy import where
import datetime
if __name__=="__main__":
    date = "12th June"
    csvLst = "C:/Users/JaydenZ/Desktop/Computer Science/Projects/Output/csv1.txt"
    df = pd.read_csv(csvLst)

    phases = [(1,0,'orange', 'Phase 1: Adelphel, Grinnaux and Charibert'),
    (2,0,'blue', 'Phase 2: King Thordan'),
    (3,0,'magenta', 'Phase 3: Nidhogg'),
    (4,0,'white', 'Phase 4: The Eyes'),
    (4,1,'white', 'Intermission: Rewind'),
    (5,0,'white', 'Phase 5: King Thordan II'),
    (6,0,'white', 'Phase 6: Nidhogg and Hraesvelgr'),
    (7,0,'white', 'Phase 7: Dragon-king Thordan'),
    (5,0,'red', 'Cleared')]

    pTrack = []
    factor = 4
    dotSize = 50
    imgdpi = 400
    plt.figure(figsize=(10,8))
    plt.title("Dragonsong's Reprise (Ultimate) Progression")
    # plt.scatter(df.index, df["PullDuration"], edgecolors='none')
    for i in phases:
        phase, intermission, color, label = i
        newdf = df[(df["LastPhase"] == phase) & (df["LastPhaseIntermission"] == intermission)]
        newdf["PullDuration"] = where((phase != 1) & ~((newdf["FightPercentage"] > 85) & (newdf["PullDuration"] > 120)) , newdf["PullDuration"] + 170, newdf["PullDuration"])
        plt.scatter(newdf.index, newdf["PullDuration"], c=color, s = 100 if color =='red' else dotSize, label=label, marker = '*' if color == 'red' else '.')
        pTrack.append(str(len(newdf.index)))

    plt.legend(loc='upper right')
    plt.text(-20,570,s ="Total tracked pulls: " + str(len(df.index)) + "".join( f"\n{phases[i][3]}: {pTrack[i]}"  for i in range(len(phases))) +
    "\nTime in Combat: " + str(datetime.timedelta(seconds=int(df["PullDuration"].sum()))))
    plt.xlabel("Pull", fontsize = 18)
    plt.ylabel("Seconds in Combat", fontsize = 18)
    plt.ylim(0, 800)
    
    plt.savefig("C:/Users/JaydenZ/Desktop/Computer Science/Projects/Output/" + date + str(dotSize) + str(imgdpi) + "_1.png", dpi=imgdpi)

  
