from numpy import dot
import pandas as pd
import csv
import matplotlib.pyplot as plt
import os
import datetime
if __name__=="__main__":
    date = "2nd October"
    csvLst = "C:/Users/JaydenZ/Desktop/Computer Science/Projects/Output/csv.txt"
    df = pd.read_csv(csvLst)

    phases = [(1,0,'blue', 'Phase 1: Living Liquid'),
    (1,1,'purple', 'Intermission: Limit Cut'),
    (2,0,'green', 'Phase 2: Brute Justice and Cruise Chaser'),
    (2,1,'grey', 'Intermission: Temporal Stasis'),
    (3,0,'magenta', 'Phase 3: Alexander Prime'),
    (4,0,'orange', 'Phase 4: Perfect Alexander'),
    (5,0,'red', 'Cleared')]

    pTrack = []
    factor = 4
    dotSize = 50
    imgdpi = 400
    plt.figure(figsize=(10,8))
    plt.title("The Epic of Alexander (Ultimate) Progression")
    # plt.scatter(df.index, df["PullDuration"], edgecolors='none')
    for i in phases:
        phase, intermission, color, label = i
        newdf = df[(df["LastPhase"] == phase) & (df["LastPhaseIntermission"] == intermission)]
        plt.scatter(newdf.index, newdf["PullDuration"], c=color, s = 100 if color =='red' else dotSize, label=label, marker = '*' if color == 'red' else '.')
        pTrack.append(str(len(newdf.index)))
    plt.legend(loc='upper left')
    plt.text(-35,550,s ="Total tracked pulls: " + str(len(df.index)) + "\nLiving Liquid: " + pTrack[0] + "\nLimit Cut: " + pTrack[1] + "\nBJCC: " + pTrack[2] + "\nTemporal Stasis: " + pTrack[3] + "\nAlexander Prime: " + pTrack[4] + "\nPerfect Alexander: " + pTrack[5] + "\nCombat Time: " + str(datetime.timedelta(seconds=int(df["PullDuration"].sum()))))
    plt.xlabel("Pull", fontsize = 18)
    plt.ylabel("Seconds in Combat", fontsize = 18)
    
    plt.savefig("C:/Users/JaydenZ/Desktop/Computer Science/Projects/Output/" + date + str(dotSize) + str(imgdpi) + ".png", dpi=imgdpi)
    