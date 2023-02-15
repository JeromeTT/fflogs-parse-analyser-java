from numpy import dot
import pandas as pd
import matplotlib.pyplot as plt
from numpy import where
import datetime

FIGHTNAME = "The Unending Coil of Bahamut (Ultimate)"
ID = "ucob1"
PHASES = [(1,0,'orange', 'Phase 1: Twintania', "o"),
    (2,0,'blue', 'Phase 2: Nael deus Darnus', "o"),
    (3,0,'purple', 'Phase 3: Bahamut Prime', "o"),
    (4,0,'red', 'Phase 4: Adds', "o"),
    (5,0,'gold', 'Phase 5: Golden Bahamut', "o"),
    (6,0,'orangered', 'Cleared', "*")]
OUTPUTLOC = "C:/Users/JaydenZ/Desktop/Computer Science/Projects/Output/"

if __name__=="__main__":
    date = "27th August"
    csvLst = f"{OUTPUTLOC}{ID}.csv"
    df = pd.read_csv(csvLst)
    pTrack = []
    factor = 4
    dotSize = 15
    imgdpi = 400

    # Edge case - FFLogs Identifies Twin wipe as Nael wipe.
    df["LastPhase"] = where((df["LastPhase"] == 2 ) & (df["PullDuration"] < 60) , 1, df["LastPhase"])

    plt.figure(figsize=(10,7))
    plt.title(f"{FIGHTNAME} Progression")
    for p in PHASES:
        phase, intermission, color, label, m = p
        newdf = df[(df["LastPhase"] == phase) & (df["LastPhaseIntermission"] == intermission)]
        plt.scatter(newdf.index, newdf["PullDuration"], c=color, s = 100 if color =='orangered' else dotSize, label=label, marker = m)
        pTrack.append(str(len(newdf.index)))

    # Handle clear
    pTrack[5] = "1"
    plt.scatter(411, 1043, c='orangered', s = 100, marker = "*")

    plt.legend(loc='upper left')
    plt.text(-12,590,s ="Total tracked pulls: " + str(len(df.index) + 1) + "".join( f"\n{PHASES[i][3]}: {pTrack[i]}"  for i in range(len(PHASES))) +
    "\nTime in Combat: " + str(datetime.timedelta(seconds=int(df["PullDuration"].sum()))))
    plt.xlabel("Pull", fontsize = 18)
    plt.ylabel("Seconds in Combat", fontsize = 18)
    plt.ylim(0, 1150)
    
    plt.savefig(f"{OUTPUTLOC}{ID}_{len(df)}.png", dpi=imgdpi)

  
