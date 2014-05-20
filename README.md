FNASampleApps
=============

Sample apps showing Flexibly Network Aware architecture for bandwidth and latency.

NetworkActivitySampple:

reads current device conditions (network bearer connected)
assigns a speed to the bearer (fast, medium or slow)
Based on the speed, the app downloads either a large medium or small image.

You can turn off the FNA so that only the larget is downloaded by clicking the checkbox.

This will also measure the RTT in realtime, and fill up the textbox.  

Requires root to use the Network Attenuator libraries.  If you'd like to use AT&T's Network Attenuator, pop me an e-mail and we'll get you the SDK agreement. It requires a rooted Samsung S3 (747).



scrollviewExample:
This application meaures the RTT as well, but in this case will change how much data is prefetched in the uncoming request (will pre-fetch more if the latency is high).



