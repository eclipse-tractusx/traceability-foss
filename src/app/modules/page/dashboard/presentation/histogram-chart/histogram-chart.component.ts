/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// TODO ADJUST THE TYPES FOR THOSE D3 VARIABLES
// ToDo: Complexity of this component is high

/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, HostListener, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import * as d3 from 'd3';
import { ceil, flatten, keys, omitBy, uniq } from 'lodash-es';
import { HistogramType } from '../../model/dashboard.model';
import { QualityTypes } from '../../model/quality-alert.model';

@Component({
  selector: 'app-histogram-chart',
  templateUrl: './histogram-chart.component.html',
  styleUrls: ['./histogram-chart.component.scss'],
})
export class HistogramChartComponent implements OnChanges {
  @ViewChild('histogramContainer', { static: true }) chart: any;
  @Input() histogramData: HistogramType[];
  @Input() groupingFilter: string;

  private offsetWidth: any;
  private fixedHeight: any;
  private margin: any;
  private width: any;
  private height: any;
  private x: any;
  private y: any;
  private svg: any;
  private chartStack: any;
  private layersBarArea: any;
  private layersBar: any;
  private yAxis: any;
  private stackedSeries: any;
  private chartLine: any;
  private legendContainer: any;
  private legend: any;
  private tooltip: any;
  private mouseLeave: any;
  private mouseOver: any;
  private mouseMove: any;
  private xAxisContainer: any;
  private xSvgContainer: any;
  private minX: any;
  private maxX: any;
  private overWidth: any;
  private xAxisWidth: any;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.histogramData && changes.histogramData.currentValue) {
      this.setChartContainerSize();
      this.updateChart(changes.histogramData.currentValue);
    }
  }

  @HostListener('window:resize', [])
  private onResize(): void {
    this.setChartContainerSize();
    this.updateChart(this.histogramData);
  }

  private setChartContainerSize(): void {
    this.offsetWidth = this.chart.nativeElement.offsetWidth;
    this.fixedHeight = 420;
    this.margin = { top: 10, right: 25, bottom: 60, left: 50 };
    this.width = this.offsetWidth - this.margin.left - this.margin.right;
    this.height = this.fixedHeight - this.margin.top - this.margin.bottom;
    if (this.offsetWidth >= 880) {
      this.xAxisWidth = 92;
    } else {
      this.xAxisWidth = this.offsetWidth < 880 && this.offsetWidth >= 814 ? 90 : 88;
    }
  }

  private updateChart(data: HistogramType[]): void {
    this.removeExistingChartFromParent();
    this.defineStack();
    this.initScales();
    this.initSvg();
    this.createTooltip();
    this.setMouseOver();
    this.setMouseMove();
    this.setMouseLeave();
    this.createLine();
    this.createStack(data);
    this.drawAxis();
  }

  private removeExistingChartFromParent(): void {
    // !!!!Caution!!!
    // Make sure not to do;
    //     d3.select('svg').remove();
    // That will clear all other SVG elements in the DOM
    d3.select(this.chart.nativeElement).selectAll('svg').remove();
  }

  private defineStack(): void {
    this.chartStack = d3.stack().keys(keys(QualityTypes));
  }

  private initScales(): void {
    this.x = d3
      .scaleBand()
      .range([0, this.histogramData.length < 5 ? this.width : this.width * 2])
      .padding(0.5);

    this.y = d3.scaleLinear().range([this.height - this.margin.bottom, this.margin.top]);
  }

  private initSvg(): void {
    this.legendContainer = d3
      .select(this.chart.nativeElement)
      .append('svg')
      .attr('class', 'legend')
      .attr('width', '100%')
      .attr('height', '30px')
      .attr('transform', 'translate(' + 0 + ',' + 5 + ')');

    this.svg = d3
      .select(this.chart.nativeElement)
      .append('svg')
      .attr('width', this.offsetWidth)
      .attr('height', this.height)
      .style('position', 'absolute')
      .style('pointer-events', 'none')
      .style('z-index', 1)
      .style('margin-top', '30px')
      .append('g')
      .classed('chart-contents', true);

    this.xAxisContainer = d3
      .select(this.chart.nativeElement)
      .append('div')
      .style('position', 'relative')
      .style('overflow-x', 'auto')
      .style('width', `${this.histogramData.length < 5 ? 100 : this.xAxisWidth}%`)
      .style('margin-top', '30px');
  }

  private createTooltip(): void {
    this.tooltip = d3
      .select(this.chart.nativeElement)
      .append('div')
      .style('background-color', '#444')
      .style('font-size', '12px')
      .style('color', '#fff')
      .style('padding', '0.5rem')
      .style('z-index', 20)
      .style('position', 'fixed')
      .style('text-align', 'center')
      .style('border-radius', '5px')
      .style('width', 'auto')
      .style('opacity', 0);
  }

  private createLine(): void {
    this.chartLine = d3
      .line()
      .x(d => this.x(d[0]) + this.x.bandwidth() / 2)
      .y(d => this.y(d[1]));
  }

  private drawAxis(): void {
    // The x-axis variable was deleted. No x-axis attributes were changed
    this.xSvgContainer
      .append('g')
      .classed('x-axis', true)
      .attr('transform', `translate(0,${this.height - this.margin.bottom})`)
      .attr('class', 'grid')
      .call(d3.axisBottom(this.x).tickSize(0));

    this.xSvgContainer
      .selectAll('.tick text')
      .attr('transform', `translate(0, 20)`)
      .attr('font-size', '12')
      .attr('font-family', 'Roboto');

    this.yAxis = this.svg
      .append('g')
      .attr('transform', `translate(${this.margin.left},15)`)
      .classed('y axis', true)
      .attr('class', 'grid')
      .call(d3.axisLeft(this.y).ticks(6).tickSize(-this.width));

    this.yAxis.selectAll('.tick line').attr('stroke-dasharray', '7').attr('stroke', '#444').attr('opacity', '.2');

    this.yAxis
      .selectAll('.tick text')
      .attr('transform', `translate(-10, 0)`)
      .attr('font-size', '12')
      .attr('font-family', 'Roboto');

    this.svg.selectAll('.grid').selectAll('.domain').remove();

    this.xSvgContainer.selectAll('.grid').selectAll('.domain').remove();
  }

  /**
   * Creates the stacked chart
   *
   * @private
   * @param {HistogramType[]} stackData
   * @return {void}
   * @memberof HistogramChartComponent
   */
  private createStack(stackData: HistogramType[]): void {
    this.stackedSeries = this.chartStack(stackData);
    this.drawChart(this.stackedSeries);
  }

  private drawChart(data: HistogramType[]): void {
    const filter = {
      Daily: 'date',
      Weekly: 'week',
      Monthly: 'month',
    };
    const key: string = filter[this.groupingFilter];
    const lineData = this.histogramData.map(values => [values[key], values.total]);

    this.x.domain(this.histogramData.map((d: any) => d[key]));

    this.y.domain([0, +d3.max(this.stackedSeries, (stack: any) => d3.max(stack, (d: any) => ceil(d[1] * 1.2)))]);

    this.minX = this.x(this.histogramData[0].date);
    this.maxX = this.x(this.histogramData[this.histogramData.length - 1].date);

    this.overWidth = this.maxX - this.minX + this.margin.left + this.margin.right + 10;

    this.xSvgContainer = this.xAxisContainer
      .append('svg')
      .style('display', 'block')
      .style('position', 'relative')
      .attr('width', this.histogramData.length < 5 ? this.offsetWidth : this.overWidth)
      .attr('height', this.height);

    this.drawStackedRectangles(data, key);
    this.drawLinePath(lineData);
    this.drawLegend();
  }

  private drawStackedRectangles(data: HistogramType[], key: string): void {
    this.layersBarArea = this.xSvgContainer.append('g').classed('layers', true);

    this.layersBar = this.layersBarArea
      .selectAll('.layer')
      .data(data)
      .enter()
      .append('g')
      .classed('layer', true)
      .style('fill', (d: any) => QualityTypes[d.key]);

    this.layersBar
      .selectAll('rect')
      .data((d: any) => d)
      .enter()
      .append('rect')
      .attr('width', this.x.bandwidth())
      .attr('height', (d: any) => this.y(d[0]) - this.y(d[1]))
      .attr('x', (d: any) => this.x(d.data[key]))
      .attr('y', (d: any) => this.y(d[1]))
      .on('mouseover', this.mouseOver)
      .on('mousemove', this.mouseMove)
      .on('mouseleave', this.mouseLeave);
  }

  private drawLinePath(lineData: any[][]): void {
    this.xSvgContainer
      .append('g')
      .classed('labels', true)
      .selectAll('text')
      .data(lineData)
      .enter()
      .append('text')
      .attr('x', (d: any) => this.x(d[0]) + this.x.bandwidth() / 2)
      .attr('y', (d: any) => this.y(d[1]))
      .attr('fill', '#444444')
      .attr('font-size', '14')
      .attr('font-family', 'Roboto')
      .attr('transform', `translate(-8, -8)`)
      .text((d: any) => d[1]);

    this.xSvgContainer
      .append('path')
      .attr('fill', 'none')
      .attr('stroke', '#acbcd7')
      .attr('stroke-width', 3)
      .attr('d', this.chartLine(lineData));
  }

  private drawLegend(): void {
    const omittedValues = this.histogramData.map(histogram => omitBy(histogram, value => value === 0));
    const mappedKeys = uniq(
      flatten(omittedValues.map(values => keys(values).filter(type => keys(QualityTypes).includes(type)))),
    );
    const sortedQualityTypes = [...mappedKeys].sort(
      (a, b) => keys(QualityTypes).indexOf(a) - keys(QualityTypes).indexOf(b),
    );

    this.legend = this.legendContainer
      .selectAll('text')
      .data(sortedQualityTypes)
      .enter()
      .append('g')
      .attr('transform', (d, i) => 'translate(' + i * 90 + ',' + 5 + ')')
      .attr('class', 'legend');

    this.legend
      .append('circle')
      .attr('r', 5)
      .attr('cx', 5)
      .attr('cy', 15)
      .attr('fill', d => QualityTypes[d]);

    this.legend
      .append('text')
      .text((d: any) => d)
      .attr('y', 10)
      .attr('x', 20)
      .attr('font-size', '12')
      .attr('text-anchor', 'start')
      .attr('alignment-baseline', 'hanging');
  }

  private setMouseOver(): void {
    this.mouseOver = () => this.tooltip.style('opacity', 1);
  }

  private setMouseLeave(): void {
    this.mouseLeave = () => this.tooltip.style('opacity', 0);
  }

  private setMouseMove(): void {
    this.mouseMove = (event, data) => {
      this.tooltip
        .text(data[1] - data[0])
        .style('left', `${event.clientX + 10}px`)
        .style('top', `${event.clientY + 15}px`)
        .style('opacity', 1);
    };
  }
}
