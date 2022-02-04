#Brainstorming
What do I want my simple application to do? Something Splatoon 2 related.
Obviously. I was obsessed with this silly game for a while, but I never
had the confidence to create something for it, and if I'm considering
writing a project for my personal enrichment, it may as well turn into a
thing that satisfies another aspect of my personal life.

Skimming available sources, I can see a few existing things and what might
be missing among those resources.

##Sendou.ink
This is a website with some of the most basic competitive organization
required for someone wanting to get into Splatoon.

###Builds
The builds page allows users to submit a given gear arrangement with main
and sub abilities which they use for a certain weapon. 

####Potential improvements:
You can't know if a given gear build is popular or still
being used. One solution to this would be to gradually scrape data of
teammates and opponents for their gear builds and update a database over
time, then usage rate can be uncovered...

In order for this to be clean, it would be best to exclude gears which
are incomplete, duplicated (ie the same user plays the same gear), or 
equivalent (same abilities, different actual shoe/shirt).

How I would store this data? Table would be something like:

Date : InGameName(Char12) : Abilities("MsssMsssMsss") : Hat : Shirt : Shoe

^can have int identifiers for the hat/shirt/shoe with an enum,
character identifier per gear ability...

###Free Agents, Teams, Users
This is effectively a public account of people looking for teams or who 
previously organized with another group of players.

### Missing
* Pugs - these are typically handled by discord.
* Visualizations - putting data from other sources into a nice viewable
source page... example would be "range" applied to planner or map view.
* ???

##What can I do?