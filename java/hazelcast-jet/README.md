# Hazelcast Jet

Jet is a solution of Hazelcast from stream based data processing. 

## What is this sample about?

- This example tries
to parse an html document downloaded from steam website with the following url:

https://store.steampowered.com/games/#tab=TopSellers

- While parsing Jet vertexes will try to look for any element where there is a price 
related information. To keep it basic it will only look any content with a currency.

- If such an element is found it will try to match with the game name and emit it to the
sink vertex as a serializable object(again for keeping it simple a class with
two fields name and price).

- Sink vertex will serialize the collected data to a json document.

## ToDo

- More complex samples.

- Ideas: 
    1. Get sample data from another gaming website and do a price comparision for
          the same game.(Not planned)
    2. Playing around parallelism factor of vertexes.(Not planned)
    3. Same project with apache storm.(Not planned)
    


