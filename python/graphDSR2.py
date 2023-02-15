# %%
from numpy import dot
import pandas as pd
import matplotlib.pyplot as plt
from numpy import where
import time
CLEARED = True
GROUPNAME = ""
FIGHTNAME = "Dragonsong's Reprise (Ultimate)"
ID = "dsr2"
PHASES = [(1,0,'orange', 'Phase 1: Adelphel, Grinnaux and Charibert', "o", "p1"),
    (2,0,'blue', 'Phase 2: King Thordan', "o", "p2"),
    (3,0,'purple', 'Phase 3: Nidhogg', "o", "p3"),
    (4,0,'red', 'Phase 4: The Eyes', "o", "p4"),
    (1,1,'gold', 'Intermission: Rewind', "D", "i1"),
    (5,0,'mediumblue', 'Phase 5: King Thordan II', "D", "p5"),
    (6,0,'darkviolet', 'Phase 6: Nidhogg and Hraesvelgr', "D", "p6"),
    (7,0,'aqua', 'Phase 7: Dragon-king Thordan', "D", "p7")]
# Enter the output location below:
# For example "C:/Users/Minty/Desktop/Output"
OUTPUTLOC = "C:/Users/JaydenZ/Desktop/Computer Science/Projects/Output/"
ROLLINGAVG = 200
COLORLIST = ["tab:blue", "tab:orange", "tab:green", "tab:red", "tab:purple", "tab:brown", "tab:pink", "tab:cyan"]

import datetime
if __name__=="__main__":
    currentTime = time.time()
    csvLst = f"{OUTPUTLOC}{ID}.csv"
    df = pd.read_csv(csvLst)
    df.drop_duplicates(inplace=True)
    df["PullDuration"] = df["PullDuration"].apply(lambda x: x/1000)

    plt.figure(figsize=(12,8))
    plt.title(f"{FIGHTNAME} Progression")

    pTrack = []
    for p in PHASES:
        phase, intermission, color, label, m, _ = p
        newdf = df[(df["LastPhase"] == phase) & (df["LastPhaseIntermission"] == intermission)]
        # Edge cases - Detecting Non - Phase 1 wipes
        newdf["PullDuration"] = where((phase != 1) & ~((newdf["FightPercentage"] > 85) & (newdf["PullDuration"] > 120)) , newdf["PullDuration"] + 170, newdf["PullDuration"])
        # Edge case - FFLogs Bug - Fight Percentage 100
        newdf["PullDuration"] = where((phase != 1) & (newdf["FightPercentage"] == 100), newdf["PullDuration"] + 170, newdf["PullDuration"])
        # Edge case - Handle Intermission
        newdf["PullDuration"] = where((phase == 1 ) & (intermission == 1) , newdf["PullDuration"] + 170, newdf["PullDuration"])
        plt.scatter(newdf.index, newdf["PullDuration"], c=color, s = 100 if color =='orangered' else 10, label=label, marker = m)
        pTrack.append(str(len(newdf.index)))

    pTrack.append("1")

    plt.scatter(2042, 1126+170, c='orangered', s = 100, marker = "*")
    plt.legend(loc='upper left')
    plt.text(-50,520,s ="Total tracked pulls: " + str(len(df.index) + CLEARED) + "".join( f"\n{PHASES[i][3]}: {pTrack[i]}"  for i in range(len(PHASES))) +
    "\nTime in Combat: " + str(datetime.timedelta(seconds=int(df["PullDuration"].sum()))))
    plt.xlabel("Pull", fontsize = 18)
    plt.ylabel("Seconds in Combat", fontsize = 18)
    plt.ylim(0, 1350)

    plt.savefig(f"{OUTPUTLOC}{ID}_{len(df)}_{currentTime}.png", dpi = 400)
    #%%

    plt.figure(figsize=(20,8))
    newdf = df[["LastPhase", "LastPhaseIntermission"]].copy()
    for phase in PHASES:
        lastPhase, lastPhaseIntermission, _, _, _, id = phase
        newdf[id] = 0
        newdf.loc[(newdf.LastPhase == lastPhase) & (newdf.LastPhaseIntermission == lastPhaseIntermission), id] = 1
        newdf[id] = newdf[id].rolling(ROLLINGAVG, min_periods=1).mean()
    
    for i in range(len(PHASES)):
        name = PHASES[i][3]
        color = COLORLIST[i]
        plt.plot([],[],color=color, label=name, linewidth=3)
    plt.legend(loc="lower left")
    plt.ylim(bottom=0, top=1)
    plt.xlim(0, len(df.index)-1)
    plt.title(f"DSR phase pull distribution (Rolling average of {ROLLINGAVG})")
    plt.stackplot(newdf.index, *[newdf[p[5]] for p in PHASES], colors=COLORLIST)
# %%
