# rsocket-replayprocessor-example
An example of storing and replaying messages when a new subscriber connects using [Project Reactor](https://projectreactor.io/) and [RSocket](http://rsocket.io).

## Building the Example
Run the following command to build the example:

    ./gradlew clean build
    
## Running the Example
Follow the steps below to run the example:

1. Run the following command to start the `count-service`:

        ./gradlew :count-service:run
        
2. In a new terminal, run the following command to start the `count-client`:

        ./gradlew :count-client:run
        
    Notice how you receive a flurry of numbers when the subscriber connects starting at `1` up to the current count. Once you are all caught up
    with the current count you start receiving a new number every one second. This is the `ReplayProcessor` making sure that a subscriber does
    not miss a published message.
    
        > Task :count-client:run
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 1
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 2
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 3
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 4
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 5
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 6
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 7
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 8

3. In a new terminal, run the following command to start another `count-client`:

        ./gradlew :count-client:run
        
    Notice how this client instance is streamed all of the published numbers as well until it is caught up.
    
4. Now, let the count reach `100` where the request will then complete.

        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 99
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Received: 100
        [reactor-tcp-nio-1] INFO example.count.client.CountClient - Done

5. Once the request has completed start a new `count-client` by again running the following command:

        ./gradlew :count-client:run
        
    Notice that you did not have to wait for the numbers to arrive in one second intervals, you were just immediately streamed all
    one hundred messages. This is because the ReplayProcessor has stored the generated stream and simply replays it back to you.
    
## Bugs and Feedback
For bugs, questions, and discussions please use the [Github Issues](https://github.com/gregwhitaker/rsocket-replayprocessor-example/issues).

## License
MIT License

Copyright (c) 2020 Greg Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.