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
import { Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import * as d3 from 'd3';
import { ceil, keys, map, min, orderBy, values } from 'lodash-es';
import { ReceivedAlertType } from '../../model/dashboard.model';
import { QualityTypes } from '../../model/quality-alert.model';

@Component({
  selector: 'app-alert-donut-chart',
  templateUrl: './alert-donut-chart.component.html',
})
export class AlertDonutChartComponent implements OnChanges {
  @Input() chartData: ReceivedAlertType[];
  @ViewChild('donutContainer', { static: true }) chart: ElementRef;

  private svg: any;
  private colorScale: any;
  private arc: any;
  private width = 400;
  private height = 300;
  private pie: any;
  private legend: any;
  private legendHolder: any;
  private tooltip: any;
  private mouseLeave: any;
  private mouseOver: any;
  private mouseMove: any;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.chartData.currentValue) {
      this.updateChart(changes.chartData.currentValue);
    }
  }

  private updateChart(data: ReceivedAlertType[]): void {
    if (!this.svg) {
      this.createChart(data);
    }
  }

  private createChart(data: ReceivedAlertType[]): void {
    this.removeExistingChartFromParent();
    this.setChartDimensions();
    this.createTooltip();
    this.setMouseOver();
    this.setMouseMove();
    this.setMouseLeave();
    this.setColorScale(data);
    this.setArcs();
    this.setPie();
    const arcs = this.pie(data);
    this.svgPath(arcs);
    this.appendLabels(arcs);
    this.appendLegends(arcs);
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
      .style('width', '150px')
      .style('opacity', 0);
  }

  private svgPath(arcs: any): void {
    this.svg
      .append('g')
      .attr('transform', 'translate(' + 0 + ',' + 30 + ')')
      .selectAll('path')
      .data(arcs)
      .join('path')
      .attr('fill', d => this.colorScale(d.data.type))
      .attr('d', this.arc)
      .on('mouseover', this.mouseOver)
      .on('mousemove', this.mouseMove)
      .on('mouseleave', this.mouseLeave);
  }

  private appendLabels(arcs: any): void {
    this.svg
      .append('g')
      .attr('transform', 'translate(0, 30)')
      .attr('font-family', 'sans-serif')
      .attr('font-size', 16)
      .attr('text-anchor', 'middle')
      .attr('font-weight', 700)
      .attr('line-height', 24)
      .selectAll('text')
      .data(arcs)
      .join('text')
      .attr('transform', d => `translate(${this.arc.centroid(d)})`)
      .call(text =>
        text
          .append('tspan')
          .attr('x', 0)
          .attr('y', '0.7em')
          .text(d => this.calculatePercentage(d.data.total))
          .style('fill', d => this.setLabelColor(d.data.type)),
      );
  }

  private appendLegends(arcs: any): void {
    this.legend = this.legendHolder
      .selectAll('legend')
      .data(arcs)
      .enter()
      .append('g')
      .attr('transform', (d, i) => 'translate(' + i * 80 + ', 5)')
      .attr('class', 'legend');

    this.legend
      .append('circle')
      .attr('cx', 5)
      .attr('cy', 5)
      .attr('r', 5)
      .attr('fill', d => this.colorScale(d.data.type));

    this.legend
      .append('text')
      .text(d => d.data.type)
      .style('font-size', 12)
      .attr('color', '#000')
      .attr('y', 10)
      .attr('x', 20);
  }

  private setChartDimensions(): void {
    this.legendHolder = d3
      .select(this.chart.nativeElement)
      .append('svg')
      .attr('width', '100%')
      .attr('height', '30px')
      .attr('transform', 'translate(0, 5)');

    this.svg = d3
      .select(this.chart.nativeElement)
      .append('svg')
      .attr('width', '100%')
      .attr('height', '350px')
      .attr('viewBox', [-this.width / 2 + 30, -this.height / 2, this.width - 50, this.height + 50].toString());
  }

  private setColorScale(data: ReceivedAlertType[]): void {
    this.colorScale = d3
      .scaleOrdinal()
      .domain(
        orderBy(
          data.map(alert => alert.type),
          ['type', 'asc'],
        ),
      )
      .range(map(values(data.filter(alert => keys(QualityTypes).includes(alert.type))), 'color'));
  }

  private setArcs(): void {
    const radius = min([this.width, this.height]) / 2;
    this.arc = d3
      .arc()
      .innerRadius(radius * 0.67)
      .outerRadius(radius - 1);
  }

  private setPie(): void {
    this.pie = d3
      .pie<ReceivedAlertType>()
      .padAngle(0.01)
      .sort(null)
      .value(d => d.total);
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
        .text(this.assembleLabel(data.data))
        .style('left', `${event.clientX + 10}px`)
        .style('top', `${event.clientY + 15}px`)
        .style('opacity', 1);
    };
  }

  private removeExistingChartFromParent(): void {
    // !!!!Caution!!!
    // Make sure not to do;
    //     d3.select('svg').remove();
    // That will clear all other SVG elements in the DOM
    d3.select(this.chart.nativeElement).select('svg').remove();
  }

  private calculatePercentage(total: number): string {
    const totalOfAlerts: number = this.chartData.map(alert => alert.total).reduce((acc, curr) => acc + curr, 0);
    return `${ceil((total / totalOfAlerts) * 100).toLocaleString()}%`;
  }

  private setLabelColor(type: string): string {
    return type.includes('LIFE-THREATENING') || type.includes('CRITICAL') ? '#fff' : '#000';
  }

  private assembleLabel(data: ReceivedAlertType): string {
    const label = data.total === 1 ? 'part' : 'parts';
    return `${data.total} ${label} with ${data.type.toLocaleLowerCase()} issues`;
  }
}
