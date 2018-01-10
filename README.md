# MineMonitor
Monitor user stats for available cryptonote-universal-pool stats

It should work with either [this](https://github.com/fancoder/cryptonote-universal-pool) or [this](https://github.com/zone117x/node-cryptonote-pool), though you will need to confirm on a case by case basis.

* Open the app
* Click the settings button on the top right.
* Enter the stats URL for your pool 
    * Note that you should use the "stats" end point, not "live_stats"
        * Examples would be [https://www.durinsmine.com](https://www.durinsmine.com:9119/stats) or [https://minexmr.com](https://p5.minexmr.com/stats)
* Enter the user stats URL for your pool (your address will be appended to the end)
* Enter your payment address
    * XMR and Aeon have been tested

* Save. 
* Refresh.

Note that you can only refresh once per minute manually. There is also a 5 minute background process that will run if the app is open (even if minimized) that will refresh and send notifications for new blocks.