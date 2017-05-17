# CANProjects

This repository consists of different projects that can be used with a CAN-bus and was designed with a CAN-bus in a car in mind.

For reading and writing to and from a CAN-bus, USBtin from fischlo.de/usbtin/ is used, along with the matching adapter.

## CANLogic

This project houses a basic CANRepository that can fetch messages from a CAN-bus and puts into a Map defined by the message ID and the message itself.

## CANAnalyzer

A handy tool to reverse engineer a CAN-bus of a car.

With this tool, one can:

  * connect to a port with baud rate and open-mode
  * list messages
  * filter for a message
  * visualize a message body
  * take notes
  
Visualization, filtering and taking notes are functions that are not supported by the
offical USBtin-GUI software, but are incredible helpful for reverse engineering.

Keep in mind that this tool is in an early stage of development. So expect perfomance issues,
failures of miscellaneous kinds and maybe wrong data over all. Nevertheless is already used it
and one can retrieve valuable data.

To access the latest, most stable as possible version, use develop-branch.

## CANPresenter

Simulates a speedometer with data from a CAN-bus.

Hardcoded to my Ford Fiesta V 2007 JD3. The IDs used are probably only
usable for this kind of vehicles that are sold in europe or - even more 
restricted - in Austria, as CAN-IDs in vehicles are very different depending on
the vehicle, its year of production, model and sometimes even countries they are
sold in.

## Libaries
### USBTinLib
#### License
USBtinLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

USBtinLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

#### Changes
No changes to the USBtinLib library were made.

#### Info
This project uses the USBtinLib library to communicate with a 
CAN-Bus via a USB-Adapter. 
The library can be downloaded from this repository under /libs/
or from http://www.fischl.de or its own 
repository https://github.com/EmbedME/USBtinLib

#### Credits
Thomas Fischl
http://www.fischl.de

